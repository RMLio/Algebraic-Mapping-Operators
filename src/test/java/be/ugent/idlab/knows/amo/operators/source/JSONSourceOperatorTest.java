package be.ugent.idlab.knows.amo.operators.source;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.NullNode;
import be.ugent.idlab.knows.amo.operators.source.dataio.JSONSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.builders.SourceOperatorBuilder;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.FieldBuilder;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.ReferenceField;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class JSONSourceOperatorTest {
//    @Test
//    @Disabled // TODO to be figured out
//    public void JSONListInField() {
//        Access access = new LocalFileAccess("operators/source/json/list.json", "src/test/resources", "json");
//        JSONSourceOperator operator = (JSONSourceOperator) SourceOperatorBuilder.JSON()
//                .withAccess(access)
//                .withFields(
//                        ExpressionField.JSON("ID", "$.ID"),
//                        ExpressionField.JSON("Sport", "$.Sport"))
//                .withRootIterator("$.students[*]")
//                .build();
//
//        MappingTuple actual = operator.consumeSource();
//
//        System.out.println(actual);
//
//    }

    @Test
    public void arrayWithoutIndexing() {
        Field items = Field.builder().JSON().withName("items").withReference("$.items").build();
        Access access = new LocalFileAccess("operators/source/json/people.json", "src/test/resources", "json");
        JSONSourceOperator operator = (JSONSourceOperator) SourceOperatorBuilder.JSON()
                .withAccess(access)
                .withRootIterator("$.people[*]")
                .withFields(List.of(items))
                .build();

        Assertions.assertThrows(IllegalArgumentException.class, operator::consumeSource);
    }

    /**
     * Tests the generation of MappingTuples with correct amount of SolutionMappings in case of arrays
     */
    @Test
    public void subfields() {
        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("alice"),
                        "item.type", new LiteralNode("sword"),
                        "item.weight", new LiteralNode(1500, XSDDatatype.XSDinteger)
                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("alice"),
                        "item.type", new LiteralNode("shield"),
                        "item.weight", new LiteralNode(2500, XSDDatatype.XSDinteger)
                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("bob"),
                        "item.type", new LiteralNode("flower"),
                        "item.weight", new LiteralNode(15, XSDDatatype.XSDinteger)
                ))
        ));

        Access access = new LocalFileAccess("operators/source/json/people.json", "src/test/resources", "json");

        Field name = Field.builder().JSON().withName("name").withReference("$.name").build();
        Field type = Field.builder().JSON().withName("type").withReference("$.type").build();
        Field weight = Field.builder().JSON().withName("weight").withReference("$.weight").build();

        Field item = Field.builder().JSON().withName("item").withIterator("$.items[*]").withSubfields(type, weight).build();

        JSONSourceOperator operator = (JSONSourceOperator) SourceOperatorBuilder.JSON()
                .withAccess(access)
                .withRootIterator("$.people[*]")
                .withFields(List.of(name, item))
                .build();

        operator.init();

        MappingTuple actual = operator.consumeSource();

        assertEquals(expected, actual);
    }

    @Test
    public void recordIndexes() {
        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                // alice sword
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("alice"),
                        "item.type", new LiteralNode("sword"),
                        "item.weight", new LiteralNode(1500, XSDDatatype.XSDinteger)
                )),
                // alice shield
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("alice"),
                        "item.type", new LiteralNode("shield"),
                        "item.weight", new LiteralNode(2500, XSDDatatype.XSDinteger)
                )),
                // bob flower
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("bob"),
                        "item.type", new LiteralNode("flower"),
                        "item.weight", new LiteralNode(15, XSDDatatype.XSDinteger)
                ))
        ));

        Field name = Field.builder().JSON().withName("name").withReference("$.name").build();
        Field type = Field.builder().JSON().withName("type").withReference("$.type").build();
        Field weight = Field.builder().JSON().withName("weight").withReference("$.weight").build();

        Field item = Field.builder().JSON().withName("item").withIterator("$.items[*]").withSubfields(type, weight).build();

        Access access = new LocalFileAccess("operators/source/json/people.json", "src/test/resources", "json");
        JSONSourceOperator operator = (JSONSourceOperator) SourceOperatorBuilder.JSON()
                .withAccess(access)
                .withRootIterator("$.people[*]")
                .withFields(List.of(name, item))
                .build();

        MappingTuple actual = operator.consumeSource();

        assertEquals(expected, actual);

        List<SolutionMapping> maps = new ArrayList<>(actual.getSolutionMappings("default"));

        SolutionMapping first = maps.getFirst();
        assertEquals(0, first.get("#").getValue());
        assertEquals(0, first.get("name.#").getValue());
        assertEquals(0, first.get("item.#").getValue());
        assertEquals(0, first.get("item.type.#").getValue());
        assertEquals(0, first.get("item.weight.#").getValue());

        SolutionMapping second = maps.get(1);
        assertEquals(0, Objects.requireNonNull(second.get("#")).getValue());
        assertEquals(0, Objects.requireNonNull(second.get("name.#")).getValue());
        assertEquals(1, Objects.requireNonNull(second.get("item.#")).getValue());
        assertEquals(0, Objects.requireNonNull(second.get("item.type.#")).getValue());
        assertEquals(0, Objects.requireNonNull(second.get("item.weight.#")).getValue());

        SolutionMapping third = maps.get(2);
        assertEquals(1, Objects.requireNonNull(third.get("#")).getValue());
        assertEquals(0, Objects.requireNonNull(third.get("name.#")).getValue());
        assertEquals(0, Objects.requireNonNull(third.get("item.#")).getValue());
        assertEquals(0, Objects.requireNonNull(third.get("item.type.#")).getValue());
        assertEquals(0, Objects.requireNonNull(third.get("item.weight.#")).getValue());
    }

    @Test
    public void deepNested() {
        Access access = new LocalFileAccess("operators/source/json/deep_nested.json", "src/test/resources", "json");

        Field weight = Field.builder().JSON().withName("weight").withReference("$.weight").build();
        Field measures = Field.builder().JSON().withName("measures").withReference("$.measures").withSubfields(weight).build();
        Field type = Field.builder().JSON().withName("type").withReference("$.type").build();
        Field items = Field.builder().JSON().withName("item").withIterator("$.items[*]").withSubfields(type, measures).build();
        Field name = Field.builder().JSON().withName("name").withReference("$.name").build();

        SourceOperator operator = SourceOperatorBuilder.JSON()
                .withAccess(access)
                .withRootIterator("$.people[*]")
                .withFields(name, items)
                .build();

        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("alice"),
                        "item.type", new LiteralNode("sword"),
                        "item.measures.weight", new LiteralNode(1500, XSDDatatype.XSDinteger)
                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("alice"),
                        "item.type", new LiteralNode("shield"),
                        "item.measures.weight", new LiteralNode(2500, XSDDatatype.XSDinteger)
                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("bob"),
                        "item.type", new LiteralNode("flower"),
                        "item.measures.weight", new LiteralNode(15, XSDDatatype.XSDinteger)
                ))
        ));

        MappingTuple actual = operator.consumeSource();

        assertEquals(expected, actual);
    }

    @Test
    public void arrayExpressionField() {
        Access access = new LocalFileAccess("operators/source/json/array.json", "src/test/resources", "json");

        Field name = Field.builder().JSON().withName("name").withReference("$.name").build();
        Field items = Field.builder().JSON().withName("items").withReference("$.items[*]").build();

        JSONSourceOperator operator = (JSONSourceOperator) SourceOperatorBuilder.JSON()
                .withAccess(access)
                .withRootIterator("$.people[*]")
                .withFields(List.of(name, items))
                .build();

        MappingTuple actual = operator.consumeSource();

        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("alice"),
                        "items", new LiteralNode("sword"),
                        "items.#", new LiteralNode(0, XSDDatatype.XSDinteger)

                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("alice"),
                        "items", new LiteralNode("shield"),
                        "items.#", new LiteralNode(1, XSDDatatype.XSDinteger)
                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("bob"),
                        "items", new LiteralNode("flower")
                ))
        ));

        assertEquals(expected, actual);
    }

    @Test
    public void fieldSpecificFormulation() {
        Field type = Field.builder().CSV().withName("type").withReference("type").build();
        Field weight = Field.builder().CSV().withName("weight").withReference("weight").build();
        Field item = Field.builder().CSV().withName("item").withSubfields(type, weight).build();

        Field items = Field.builder().JSON().withName("items").withIterator("$.items").withSubfields(item).build();
        Field name = Field.builder().JSON().withName("name").withReference("$.name").build();

        Access access = new LocalFileAccess("operators/source/json/different_formulation.json", "src/test/resources", "json");
        JSONSourceOperator operator = (JSONSourceOperator) SourceOperatorBuilder.JSON()
                .withAccess(access)
                .withRootIterator("$.people[*]")
                .withFields(List.of(name, items))
                .build();

        MappingTuple actual = operator.consumeSource();

        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("alice"),
                        "items.item.type", new LiteralNode("sword"),
                        "items.item.weight", new LiteralNode("1500")
                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("alice"),
                        "items.item.type", new LiteralNode("shield"),
                        "items.item.weight", new LiteralNode("2500")
                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("bob"),
                        "items.item.type", new LiteralNode("flower"),
                        "items.item.weight", new LiteralNode("15")
                ))
        ));

        assertEquals(expected, actual);
    }

    @Test
    public void gzipCompression() {
        Access access = new LocalFileAccess("operators/source/json/Friends.json.gz", "src/test/resources", "gzip");

        Field name = Field.builder().JSON().withName("name").withReference("$.name").build();
        SourceOperator op = SourceOperatorBuilder.JSON()
                .withRootIterator("$.[*]")
                .withFields(name)
                .withAccess(access)
                .withCompression(Compression.GZip)
                .build();

        MappingTuple actual = op.consumeSource();
        System.out.println(actual);
    }
}

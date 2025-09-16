package be.ugent.idlab.knows.amo.operators.source;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.NullNode;
import be.ugent.idlab.knows.amo.operators.source.dataio.JSONSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
        JSONSourceOperator operator = new JSONSourceOperator(
                "source operator",
                access, Set.of("default"),
                "$.people[*]",
                List.of(items),
                List.of());

        /*operator.init();
        MappingTuple actual = operator.consumeSource();*/

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

        JSONSourceOperator operator = new JSONSourceOperator(
                "source operator",
                access, Set.of("default"),
                "$.people[*]",
                List.of(name, item),
                List.of());

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
        JSONSourceOperator operator = new JSONSourceOperator(
                "source operator",
                access, Set.of("default"),
                "$.people[*]",
                List.of(name, item),
                List.of());

        MappingTuple actual = operator.consumeSource();

        assertEquals(expected, actual);

        List<SolutionMapping> maps = new ArrayList<>(actual.getSolutionMappings("default"));

        SolutionMapping first = maps.getFirst();
        assertEquals(0, ((LiteralNode)first.get("#")).getValueObject());
        assertEquals(0, ((LiteralNode)first.get("name.#")).getValueObject());
        assertEquals(0, ((LiteralNode)first.get("item.#")).getValueObject());
        assertEquals(0, ((LiteralNode)first.get("item.type.#")).getValueObject());
        assertEquals(0, ((LiteralNode)first.get("item.weight.#")).getValueObject());

        SolutionMapping second = maps.get(1);
        assertEquals(0, Objects.requireNonNull(((LiteralNode)second.get("#")).getValueObject()));
        assertEquals(0, Objects.requireNonNull(((LiteralNode)second.get("name.#")).getValueObject()));
        assertEquals(1, Objects.requireNonNull(((LiteralNode)second.get("item.#")).getValueObject()));
        assertEquals(0, Objects.requireNonNull(((LiteralNode)second.get("item.type.#")).getValueObject()));
        assertEquals(0, Objects.requireNonNull(((LiteralNode)second.get("item.weight.#")).getValueObject()));

        SolutionMapping third = maps.get(2);
        assertEquals(1, Objects.requireNonNull(((LiteralNode)third.get("#")).getValueObject()));
        assertEquals(0, Objects.requireNonNull(((LiteralNode)third.get("name.#")).getValueObject()));
        assertEquals(0, Objects.requireNonNull(((LiteralNode)third.get("item.#")).getValueObject()));
        assertEquals(0, Objects.requireNonNull(((LiteralNode)third.get("item.type.#")).getValueObject()));
        assertEquals(0, Objects.requireNonNull(((LiteralNode)third.get("item.weight.#")).getValueObject()));
    }

    @Test
    public void deepNested() {
        Access access = new LocalFileAccess("operators/source/json/deep_nested.json", "src/test/resources", "json");

        Field weight = Field.builder().JSON().withName("weight").withReference("$.weight").build();
        Field measures = Field.builder().JSON().withName("measures").withReference("$.measures").withSubfields(weight).build();
        Field type = Field.builder().JSON().withName("type").withReference("$.type").build();
        Field items = Field.builder().JSON().withName("item").withIterator("$.items[*]").withSubfields(type, measures).build();
        Field name = Field.builder().JSON().withName("name").withReference("$.name").build();

        SourceOperator operator = new JSONSourceOperator(
                "source operator",
                access, Set.of("default"),
                "$.people[*]",
                List.of(name, items),
                List.of());

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

        JSONSourceOperator operator = new JSONSourceOperator(
                "source operator",
                access, Set.of("default"),
                "$.people[*]",
                List.of(name, items),
                List.of());

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
        JSONSourceOperator operator = new JSONSourceOperator(
                "source operator",
                access, Set.of("default"),
                "$.people[*]",
                List.of(name, items),
                List.of());

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
    public void complexJSONArray() {
        Access access = new LocalFileAccess("operators/source/json/complex_array.json", "src/test/resources", "json");
        Field departmentName = Field.builder().JSON().withName("dep_name").withReference("$.departments[*].name").build();
        Field name = Field.builder().JSON().withName("name").withReference("$.name").build();
        JSONSourceOperator operator = new JSONSourceOperator(
                "source operator",
                access,
                Set.of("default"),
                "$.companies[*]",
                List.of(departmentName, name),
                List.of());

        MappingTuple actual = operator.consumeSource();

        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("TechCorp"),
                        "dep_name", new LiteralNode("Engineering")
                )), new SolutionMapping(Map.of(
                        "name", new LiteralNode("TechCorp"),
                        "dep_name", new LiteralNode("Marketing")
                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("InnovateX"),
                        "dep_name", new LiteralNode("Research & Development")
                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("InnovateX"),
                        "dep_name", new LiteralNode("Sales")
                ))
        ));

        assertEquals(expected, actual);
    }

    @Test
    public void null_node() {
        Access access = new LocalFileAccess("operators/source/json/null_value.json", "src/test/resources", "json");
        Field id = Field.builder().JSON().withName("ID").withReference("ID").build();
        Field name = Field.builder().JSON().withName("Name").withReference("Name").build();
        Field dob = Field.builder().JSON().withName("DateOfBirth").withReference("DateOfBirth").build();
        JSONSourceOperator operator = new JSONSourceOperator(
                "source operator",
                access,
                Set.of("default"),
                "$.persons[*]",
                List.of(id, name, dob),
                List.of());

        MappingTuple actual = operator.consumeSource();

        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                        "ID", new LiteralNode("1"),
                        "Name", new LiteralNode("Alice"),
                        "DateOfBirth", new NullNode()
                )),
                new SolutionMapping(Map.of(
                        "ID", new LiteralNode("2"),
                        "Name", new LiteralNode("Bob"),
                        "DateOfBirth", new LiteralNode("September, 2010")
                ))
        ));

        assertEquals(expected, actual);
    }

    @Test
    public void missingFields() {
        Access access = new LocalFileAccess("operators/source/json/missing_values.json", "src/test/resources", "json");

        Field id = Field.builder().JSON().withName("ID").withReference("ID").build();
        Field sport = Field.builder().JSON().withName("Sport").withReference("Sport").build();
        Field name = Field.builder().JSON().withName("Name").withReference("Name").build();

        JSONSourceOperator operator =  new JSONSourceOperator(
                "source operator",
                access,
                Set.of("default"),
                "$.students[*]",
                List.of(id, sport, name),
                List.of());

        MappingTuple actual = operator.consumeSource();
        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                        "ID", new LiteralNode(10, XSDDatatype.XSDinteger),
                        "Sport", new LiteralNode(100, XSDDatatype.XSDinteger),
                        "Name", new LiteralNode("Venus Williams")
                )),
                new SolutionMapping(Map.of(
                        "ID", new LiteralNode(20, XSDDatatype.XSDinteger),
                        "Sport", new NullNode(),
                        "Name", new LiteralNode("Demi Moore")
                ))
        ));

        assertEquals(expected, actual);
    }
}

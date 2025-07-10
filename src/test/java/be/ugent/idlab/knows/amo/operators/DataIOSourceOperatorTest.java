package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.NullNode;
import be.ugent.idlab.knows.amo.operators.source.SourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.CSVSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.JSONSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.XMLSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.builders.SourceOperatorBuilder;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.ExpressionField;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.IterableField;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DataIOSourceOperatorTest {

    @Test
    public void JSONTestSingleField() {
        Access access = new LocalFileAccess("operators/source/input.json", "src/test/resources", "json");
        JSONSourceOperator operator = (JSONSourceOperator) SourceOperatorBuilder.JSON()
                .withAccess(access)
                .withFields(List.of(new ExpressionField("name", "$.name"), new ExpressionField("pet.type", "$.pet.type"), new ExpressionField("pet.name", "$.pet.name")))
//                .withRootVariables(List.of("name"))
                .withRootIterator("$.peoples[*]")
                .build();

        MappingTuple actual = operator.consumeSource();

        MappingTuple expected = BlocksIO.readMappingTuple("operators/source/output.json");

        assertEquals(expected, actual);
    }

    @Test
    public void JSONTestMultipleField() {
        Access access = new LocalFileAccess("operators/source/input.json", "src/test/resources", "json");
        JSONSourceOperator operator = (JSONSourceOperator) SourceOperatorBuilder.JSON()
                .withAccess(access)
                .withFields(List.of(new ExpressionField("name", "$.name"), new ExpressionField("pet.type", "$.pet.type"), new ExpressionField("pet.name", "$.pet.name")))
                .withRootIterator("$.peoples[*]")
                .build();

        MappingTuple actual = operator.consumeSource();

        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("John Doe"),
                        "pet.type", new LiteralNode("dog"),
                        "pet.name", new LiteralNode("Bax")
                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("Susan Sue")
                ))
        ));

        assertEquals(expected, actual);
    }

    @Test
    public void JSONListInField() {
        Access access = new LocalFileAccess("operators/source/list.json", "src/test/resources", "json");
        JSONSourceOperator operator = (JSONSourceOperator) SourceOperatorBuilder.JSON()
                .withAccess(access)
                .withFields(List.of(new ExpressionField("ID", "$.ID"), new ExpressionField("Sport", "$.Sport")))
                .withRootIterator("$.students[*]")
                .build();

        MappingTuple actual = operator.consumeSource();

        System.out.println(actual);

    }

    @Test
    public void CSVTest() {
        Access access = new LocalFileAccess("operators/source/input.csv", "src/test/resources", "csv");
        CSVSourceOperator operator = (CSVSourceOperator) SourceOperatorBuilder.CSV()
                .withAccess(access)
                .build();

        MappingTuple actual = operator.consumeSource();
        MappingTuple expected = BlocksIO.readMappingTuple("operators/source/output.json");

        assertEquals(expected, actual);
    }

    @Test
    public void XMLTest() {
        Access access = new LocalFileAccess("operators/source/input.xml", "src/test/resources", "xml");
        XMLSourceOperator operator = (XMLSourceOperator) SourceOperatorBuilder.XML()
                .withAccess(access)
                .withRootVariable("name")
                .withRootIterator("/people/person")
                .withSubIterators(List.of("pet/type", "pet/name"))
                .build();

        MappingTuple actual = operator.consumeSource();
        MappingTuple expected = BlocksIO.readMappingTuple("operators/source/output_xml.json");

        assertEquals(expected, actual);
    }

    @Test
    public void defaultValueCSV() throws SQLException, IOException, ParserConfigurationException, TransformerException {
        Access access = new LocalFileAccess("operators/source/input.csv", "src/test/resources", "csv");
        CSVSourceOperator operator = (CSVSourceOperator) SourceOperatorBuilder.CSV()
                .withAccess(access)
                .withDefaultStringValue("foo", "bar")
                .build();
        operator.init();

        MappingTuple result = operator.consumeSource();

        assertEquals("bar", result.getSolutionMappings("default").stream().findFirst().get().get("foo").getValue());
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
                        "item.weight", new LiteralNode(1500)
                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("alice"),
                        "item.type", new LiteralNode("shield"),
                        "item.weight", new LiteralNode(2500)
                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("bob"),
                        "item.type", new LiteralNode("flower"),
                        "item.weight", new LiteralNode(15)
                ))
        ));

        Access access = new LocalFileAccess("operators/source/people.json", "src/test/resources", "json");

        ExpressionField name = new ExpressionField("name", "$.name");
        IterableField item = new IterableField("item", "$.items[*]");
        ExpressionField type = new ExpressionField("type", "$.type");
        ExpressionField weight = new ExpressionField("weight", "$.weight");

        item.getSubfields().addAll(List.of(type, weight));

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
                        "item.weight", new LiteralNode(1500)
                )),
                // alice shield
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("alice"),
                        "item.type", new LiteralNode("shield"),
                        "item.weight", new LiteralNode(2500)
                )),
                // bob flower
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("bob"),
                        "item.type", new LiteralNode("flower"),
                        "item.weight", new LiteralNode(15)
                ))
        ));

        ExpressionField name = new ExpressionField("name", "$.name");
        IterableField item = new IterableField("item", "$.items[*]");
        ExpressionField type = new ExpressionField("type", "$.type");
        ExpressionField weight = new ExpressionField("weight", "$.weight");

        item.getSubfields().addAll(List.of(type, weight));

        Access access = new LocalFileAccess("operators/source/people.json", "src/test/resources", "json");
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
        Access access = new LocalFileAccess("operators/source/deep_nested.json", "src/test/resources", "json");

        Field weight = new ExpressionField("weight", "$.weight");
        Field length = new ExpressionField("length", "$.length");
        Field measures = new IterableField("measures", "$.measures", List.of(weight, length));
        Field items = new IterableField("item", "$.items", List.of(measures));

        SourceOperator operator = SourceOperatorBuilder.JSON()
                .withAccess(access)
                .withRootIterator("$.people[*]")
                .withFields(items)
                .build();

        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                        "#", new LiteralNode(0, XSDDatatype.XSDinteger),
                        "item.#", new LiteralNode(0, XSDDatatype.XSDinteger),
                        "item.measures.#", new LiteralNode(0, XSDDatatype.XSDinteger),
                        "item.measures.weight", new LiteralNode(1500),
                        "item.measures.weight.#", new LiteralNode(0, XSDDatatype.XSDinteger),
                        "item.measures.length", new LiteralNode(30),
                        "item.measures.length.#", new LiteralNode(0, XSDDatatype.XSDinteger)
                )),
                new SolutionMapping(Map.of(
                        "#", new LiteralNode(0, XSDDatatype.XSDinteger),
                        "item.#", new LiteralNode(1, XSDDatatype.XSDinteger),
                        "item.measures.#", new LiteralNode(0, XSDDatatype.XSDinteger),

                        "item.measures.weight", new LiteralNode(2500),
                        "item.measures.weight.#", new LiteralNode(0, XSDDatatype.XSDinteger),

                        "item.measures.length", new NullNode(),
                        "item.measures.length.#", new LiteralNode(0, XSDDatatype.XSDinteger)

                )),
                new SolutionMapping(Map.of(
                        "#", new LiteralNode(1, XSDDatatype.XSDinteger),

                        "item.measures.weight", new LiteralNode(15),
                        "item.measures.length", new LiteralNode(15)
                ))
        ));

        MappingTuple actual = operator.consumeSource();

        assertEquals(expected, actual);
    }
}

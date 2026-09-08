package be.ugent.idlab.knows.amo.operators.source;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.NullNode;
import be.ugent.idlab.knows.amo.operators.source.dataio.CSVSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CSVSourceOperatorTest {

    @Test
    public void simpleTest() {
        Field name = Field.builder().withName("name").CSV().withReference("name").build();

        Field age = Field.builder().CSV().withName("age").withReference("age").build();
        Field items = Field.builder().CSV().withName("item").withSubfields(name, age).build();

        Access access = new LocalFileAccess("operators/source/csv/simple.csv", "src/test/resources", "csv");
        SourceOperator op = new CSVSourceOperator(
                "CSV Source Operator",
                access,
                Set.of("default"),
                Set.of(items),
                Set.of()
        );

        MappingTuple actual = op.consumeSource();

        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                        "item.name", new LiteralNode("John"),
                        "item.age", new LiteralNode("20")
                ))
        ));

        assertEquals(expected, actual);
    }

    @Test
    public void itemIndexes() {
        Field type = Field.builder().CSV().withName("type").withReference("type").build();
        Field weight = Field.builder().CSV().withName("weight").withReference("weight").build();
        Field items = Field.builder().CSV().withName("item").withSubfields(type, weight).build();

        Access access = new LocalFileAccess("operators/source/csv/indexes.csv", "src/test/resources", "csv");
        SourceOperator op = new CSVSourceOperator(
                "CSV Source Operator",
                access,
                Set.of("default"),
                Set.of(items),
                Set.of()
        );

        MappingTuple actual = op.consumeSource();

        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                        "#", new LiteralNode(0, XSDDatatype.XSDnonNegativeInteger),
                        "item.type", new LiteralNode("sword"),
                        "item.weight", new LiteralNode("1500"),
                        "item.#", new LiteralNode(0, XSDDatatype.XSDnonNegativeInteger),
                        "item.type.#", new LiteralNode(0, XSDDatatype.XSDnonNegativeInteger),
                        "item.weight.#", new LiteralNode(0, XSDDatatype.XSDnonNegativeInteger)
                )),
                new SolutionMapping(Map.of(
                        "#", new LiteralNode(1, XSDDatatype.XSDnonNegativeInteger),
                        "item.type", new LiteralNode("shield"),
                        "item.weight", new LiteralNode("2500"),
                        "item.#", new LiteralNode(0, XSDDatatype.XSDnonNegativeInteger),
                        "item.type.#", new LiteralNode(0, XSDDatatype.XSDnonNegativeInteger),
                        "item.weight.#", new LiteralNode(0, XSDDatatype.XSDnonNegativeInteger)
                ))
        ));

        assertEquals(expected, actual);
    }

    @Test
    public void defaultValues() {
        Field defaultValue = Field.builder().CSV().withName("defaultValue").withConstant(new LiteralNode("default")).build();
        Field name = Field.builder().CSV().withName("name").withReference("name").build();
        Field age = Field.builder().CSV().withName("age").withReference("age").build();
        Field items = Field.builder().CSV().withName("item").withSubfields(name, age).build();

        Access access = new LocalFileAccess("operators/source/csv/simple.csv", "src/test/resources", "csv");
        SourceOperator op = new CSVSourceOperator(
                "CSV Source Operator",
                access,
                Set.of("default"),
                Set.of(defaultValue, items),
                Set.of()
        );

        MappingTuple actual = op.consumeSource();

        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                        "defaultValue", new LiteralNode("default"),
                        "item.name", new LiteralNode("John")
                ))
        ));

        assertEquals(expected, actual);
    }

    @Test
    public void nestedJSONArray() {
        Field type = Field.builder().JSON().withName("type").withReference("$.type").build();
        Field weight = Field.builder().JSON().withName("weight").withReference("$.weight").build();

        Field item = Field.builder().JSON().withIterator("$[*]").withName("item").withSubfields(type, weight).build();
        Field items = Field.builder().CSV().withName("items").withReference("items").withSubfields(item).build();
        Field name = Field.builder().CSV().withName("name").withReference("name").build();

        Access access = new LocalFileAccess("operators/source/csv/nested_json_array.csv", "src/test/resources", "csv");
        SourceOperator op = new CSVSourceOperator(
                "CSV Source Operator",
                access,
                Set.of("default"),
                Set.of(name, items),
                Set.of()
        );

        MappingTuple actual = op.consumeSource();

        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("alice"),
                        "items.item.type", new LiteralNode("sword"),
                        "items.item.weight", new LiteralNode(1500, XSDDatatype.XSDint)
                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("alice"),
                        "items.item.type", new LiteralNode("shield"),
                        "items.item.weight", new LiteralNode(2500, XSDDatatype.XSDint)
                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("bob"),
                        "items.item.type", new LiteralNode("flower"),
                        "items.item.weight", new LiteralNode(15, XSDDatatype.XSDint)
                ))
        ));

        assertEquals(expected, actual);
    }

    @Test
    public void nestedJSONObject() {
        Field type = Field.builder().JSON().withName("type").withReference("$.type").build();
        Field weight = Field.builder().JSON().withName("weight").withReference("$.weight").build();
        Field itemJson = Field.builder().JSON().withName("itemJson").withIterator("$").withSubfields(type, weight).build();
        Field item = Field.builder().CSV().withName("item").withReference("item").withSubfields(itemJson).build();

        Field name = Field.builder().CSV().withName("name").withReference("name").build();
        Access access = new LocalFileAccess("operators/source/csv/nested_json_object.csv", "src/test/resources", "csv");

        SourceOperator op = new CSVSourceOperator(
                "CSV Source Operator",
                access,
                Set.of("default"),
                Set.of(name, item),
                Set.of()
        );

        MappingTuple actual = op.consumeSource();

        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("alice"),
                        "item.itemJson.type", new LiteralNode("sword"),
                        "item.itemJson.weight", new LiteralNode(1500, XSDDatatype.XSDint)
                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("alice"),
                        "item.itemJson.type", new LiteralNode("shield"),
                        "item.itemJson.weight", new LiteralNode(2500, XSDDatatype.XSDint)
                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("bob"),
                        "item.itemJson.type", new LiteralNode("flower"),
                        "item.itemJson.weight", new LiteralNode(15, XSDDatatype.XSDint)
                ))
        ));
        assertEquals(expected, actual);
    }

    @Test
    public void emptyValues() {
        Field id = Field.builder().CSV().withName("id").withReference("id").build();
        Field name = Field.builder().CSV().withName("age").withReference("age").build();
        Field age = Field.builder().CSV().withName("name").withReference("name").build();
        Field items = Field.builder().CSV().withName("item").withSubfields(id, name, age).build();

        Access access = new LocalFileAccess("operators/source/csv/empty_values.csv", "src/test/resources", "csv");
        SourceOperator op = new CSVSourceOperator(
                "CSV Source Operator",
                access,
                Set.of("default"),
                Set.of(items),
                Set.of()
        );

        MappingTuple actual = op.consumeSource();

        MappingTuple expected = new MappingTuple();
        expected.addSolutionMap("default", new SolutionMapping(Map.of(
                "item.id", new LiteralNode("5"),
                "item.name", new NullNode(),
                "item.age", new NullNode()
        )));

        assertEquals(expected, actual);
    }

    @Test
    public void nullValues() {
        Field id = Field.builder().CSV().withName("id").withReference("id").build();
        Field name = Field.builder().CSV().withName("age").withReference("age").build();
        Field age = Field.builder().CSV().withName("name").withReference("name").build();
        Field items = Field.builder().CSV().withName("item").withSubfields(id, name, age).build();

        Access access = new LocalFileAccess("operators/source/csv/null_values.csv", "src/test/resources", "csv");
        SourceOperator op = new CSVSourceOperator(
                "CSV Source Operator",
                access,
                Set.of("default"),
                Set.of(items),
                Set.of("NULL")
        );

        MappingTuple actual = op.consumeSource();

        MappingTuple expected = new MappingTuple();
        expected.addSolutionMap("default", new SolutionMapping(Map.of(
                "item.id", new LiteralNode("5"),
                "item.name", new NullNode(),
                "item.age", new NullNode()
        )));

        assertEquals(expected, actual);
    }

    @Test
    public void aDialectSaysHowTheRowsAreWritten() {
        // the rows are not written the way a plain CSV is: the columns are separated by a
        // semicolon, a value is quoted with ' rather than ", and "NULL" stands for no value
        Field name = Field.builder().withName("name").CSV().withReference("name").build();
        Field age = Field.builder().CSV().withName("age").withReference("age").build();
        Field items = Field.builder().CSV().withName("item").withSubfields(name, age).build();

        CSVWConfiguration dialect = CSVWConfiguration.builder()
                .withDelimiter(';')
                .withQuoteCharacter('\'')
                .withNulls(Set.of("NULL"))
                .build();

        Access access = new LocalFileAccess("operators/source/csv/dialect.csv", "src/test/resources", "csv");
        SourceOperator op = new CSVSourceOperator(
                "CSV Source Operator",
                access,
                Set.of("default"),
                Set.of(items),
                Set.of(),
                dialect
        );

        MappingTuple actual = op.consumeSource();

        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                        // the semicolon inside the quotes is part of the value, not a separator
                        "item.name", new LiteralNode("John;Doe"),
                        "item.age", new LiteralNode("20")
                )),
                new SolutionMapping(Map.of(
                        "item.name", new LiteralNode("Jane"),
                        "item.age", new NullNode()
                ))
        ));

        assertEquals(expected, actual);
    }
}

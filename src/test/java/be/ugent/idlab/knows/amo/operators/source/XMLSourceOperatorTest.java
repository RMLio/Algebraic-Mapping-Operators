package be.ugent.idlab.knows.amo.operators.source;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.operators.source.dataio.XMLSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.builders.SourceOperatorBuilder;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XMLSourceOperatorTest {

    @Test
    public void simpleTest() {
        Field name = Field.builder().XML().withName("ID").withReference("ID").build();

        Field age = Field.builder().XML().withName("Name").withReference("Name").build();
        Field items = Field.builder().XML().withName("item").withSubfields(name, age).build();

        Access access = new LocalFileAccess("operators/source/xml/input.xml", "src/test/resources", "xml");
        XMLSourceOperator operator = (XMLSourceOperator) SourceOperatorBuilder.XML()
                .withAccess(access)
                .withRootIterator("/students/student")
                .withField(items)
                .build();

        MappingTuple actual = operator.consumeSource();
        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                        "item.Name", new LiteralNode("Venus"),
                        "item.ID", new LiteralNode("10")
                ))
        ));

        assertEquals(expected, actual);
    }
}

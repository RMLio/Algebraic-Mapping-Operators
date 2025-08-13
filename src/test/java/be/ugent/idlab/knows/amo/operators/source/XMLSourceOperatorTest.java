package be.ugent.idlab.knows.amo.operators.source;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.operators.source.dataio.XMLSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.builders.SourceOperatorBuilder;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import org.junit.jupiter.api.Disabled;
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

    @Disabled("Parent references are not supported, implementation pending (see issues on Gitlab)")
    @Test
    public void complexSource() {
        Access access = new LocalFileAccess("operators/source/xml/complex_source.xml", "src/test/resources", "xml");

        Field managerName = Field.builder().XML().withName("manager/name").withReference("manager/name").build();
        Field id = Field.builder().XML().withName("id").withReference("../../@id").build();

        XMLSourceOperator operator = (XMLSourceOperator) SourceOperatorBuilder.XML()
                .withAccess(access)
                .withFields(managerName, id)
                .withRootIterator("/companies/company/departments/department")
                .build();

        MappingTuple actual = operator.consumeSource();

        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                    "manager/name", new LiteralNode("Alice Johnson"),
                        "id", new LiteralNode("25")
                )),new SolutionMapping(Map.of(
                        "manager/name", new LiteralNode("John Doe"),
                        "id", new LiteralNode("25")
                )),
                new SolutionMapping(Map.of(
                        "manager/name", new LiteralNode("Emma Wilson"),
                        "id", new LiteralNode("35")
                )),
                new SolutionMapping(Map.of(
                        "manager/name", new LiteralNode("Michael Green"),
                        "id", new LiteralNode("35")
                ))

                ));

        assertEquals(expected, actual);
        assertEquals(4, actual.getSolutionMappings("default").size());
    }
}

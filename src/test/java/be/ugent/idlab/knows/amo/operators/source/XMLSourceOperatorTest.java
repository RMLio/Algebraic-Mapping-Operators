package be.ugent.idlab.knows.amo.operators.source;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.operators.source.dataio.XMLSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XMLSourceOperatorTest {
    @Test
    public void simpleTest() {
        Access access = new LocalFileAccess("operators/source/xml/complex_source.xml", "src/test/resources", "xml");

        Field managerName = Field.builder().XML().withName("name").withReference("name").build();
        Field id = Field.builder().XML().withName("id").withReference("@id").build();

        XMLSourceOperator operator = new XMLSourceOperator(
                "XML Source Operator",
                access,
                Set.of("default"),
                "/companies/company",
                List.of(managerName, id),
                List.of()
        );

        MappingTuple actual = operator.consumeSource();

        MappingTuple expected = new MappingTuple();
        expected.setSolutionMaps("default", List.of(
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("TechCorp"),
                        "id", new LiteralNode("25")
                )),
                new SolutionMapping(Map.of(
                        "name", new LiteralNode("InnovateX"),
                        "id", new LiteralNode("35")
                ))
        ));

        assertEquals(expected, actual);
        assertEquals(2, actual.getSolutionMappings("default").size());
    }
}

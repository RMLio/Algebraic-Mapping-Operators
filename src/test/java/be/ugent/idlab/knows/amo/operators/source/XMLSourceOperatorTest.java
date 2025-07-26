package be.ugent.idlab.knows.amo.operators.source;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.operators.source.dataio.XMLSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.builders.SourceOperatorBuilder;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XMLSourceOperatorTest {

    @Test
    public void simpleTest() {
        Access access = new LocalFileAccess("operators/source/xml/input.xml", "src/test/resources", "xml");
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
}

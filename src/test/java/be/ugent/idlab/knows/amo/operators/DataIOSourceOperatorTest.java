package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.operators.source.dataio.CSVSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.JSONSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.XMLSourceOperator;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DataIOSourceOperatorTest {

    @Test
    public void JSONTest() {
        Access access = new LocalFileAccess("operators/source/input.json", "src/test/resources", "json");
        JSONSourceOperator operator = new JSONSourceOperator(access, List.of("name"), "$.peoples[*]", List.of("$.pet.type", "$.pet.name"));

        MappingTuple actual = operator.consumeSource();

        MappingTuple expected = BlocksIO.readMappingTuple("operators/source/output.json");

        assertEquals(expected, actual);
    }

    @Test
    public void CSVTest() {
        Access access = new LocalFileAccess("operators/source/input.csv", "src/test/resources", "csv");
        CSVSourceOperator operator = new CSVSourceOperator(access);

        MappingTuple actual = operator.consumeSource();
        MappingTuple expected = BlocksIO.readMappingTuple("operators/source/output.json");

        assertEquals(expected, actual);
    }

    @Test
    public void XMLTest() {
        Access access = new LocalFileAccess("operators/source/input.xml", "src/test/resources", "xml");
        XMLSourceOperator operator = new XMLSourceOperator(access, List.of("name"), "/people/person", List.of("pet/type", "pet/name"));

        MappingTuple actual = operator.consumeSource();
        MappingTuple expected = BlocksIO.readMappingTuple("operators/source/output_xml.json");

        assertEquals(expected, actual);
    }
}

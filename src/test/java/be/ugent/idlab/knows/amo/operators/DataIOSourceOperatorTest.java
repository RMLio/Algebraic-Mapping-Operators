package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.operators.source.CSVSourceOperatorDataIO;
import be.ugent.idlab.knows.amo.operators.source.JSONSourceOperatorDataIO;
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
        JSONSourceOperatorDataIO operator = new JSONSourceOperatorDataIO(access, List.of("name"), "$.peoples[*]", List.of("$.pet.type", "$.pet.name"));

        MappingTuple actual = operator.getMappingTuples().stream().toList().get(0);

        MappingTuple expected = BlocksIO.readMappingTuple("operators/source/output.json");

        assertEquals(expected, actual);
    }

    @Test
    public void CSVTest() {
        Access access = new LocalFileAccess("operators/source/input.csv", "src/test/resources", "csv");
        CSVSourceOperatorDataIO operator = new CSVSourceOperatorDataIO(access);

        MappingTuple actual = operator.getMappingTuples().stream().toList().get(0);
        MappingTuple expected = BlocksIO.readMappingTuple("operators/source/output.json");

        assertEquals(expected, actual);
    }
}

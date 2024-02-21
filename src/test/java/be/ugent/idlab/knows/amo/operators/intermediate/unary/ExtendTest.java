package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.intermediate.unary.ExtendOperator;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExtendTest {

    // simple condition to concatenate first and last names
    ExtendOperator op = new ExtendOperator("?full_name", (m) -> {
        String first = (String) m.get("?first_name");
        String last = (String) m.get("?last_name");

        return first + " " + last;
    });


    @Test
    public void simpleTestSolMapping() {
        SolutionMapping input = BlocksIO.readSolutionMapping("src/test/resources/operators/extend/solutionMap/input.json");
        SolutionMapping expected = BlocksIO.readSolutionMapping("src/test/resources/operators/extend/solutionMap/output.json");
        SolutionMapping output = op.applySolMapping(input);

        assertEquals(expected, output);
    }

    @Test
    public void simpleTestMappingTuple() {
        MappingTuple input = BlocksIO.readMappingTuple("src/test/resources/operators/extend/mappingTuple/input.json");
        MappingTuple expected = BlocksIO.readMappingTuple("src/test/resources/operators/extend/mappingTuple/output.json");

        MappingTuple actual = op.applyMappingTuple(input);
        assertEquals(expected, actual);
    }
}

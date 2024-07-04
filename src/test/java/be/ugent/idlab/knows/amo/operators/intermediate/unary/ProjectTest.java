package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectTest {
    @Test
    public void simpleTestSolutionMapping() {
        ProjectOperator operator = new ProjectOperator("ProjectOp", "f_default", List.of("?age"));

        SolutionMapping input = BlocksIO.readSolutionMapping("operators/project/solutionMapping/input.json");
        SolutionMapping expected = BlocksIO.readSolutionMapping("operators/project/solutionMapping/output.json");

        SolutionMapping actual = operator.apply(input);

        assertEquals(expected, actual);
    }

    @Test
    public void simpleTestMappingTuple() {
        ProjectOperator operator = new ProjectOperator("ProjectOp", "f_default", List.of("?age"));

        MappingTuple input = BlocksIO.readMappingTuple("operators/project/mappingTuple/input.json");
        MappingTuple expected = BlocksIO.readMappingTuple("operators/project/mappingTuple/output.json");
        MappingTuple actual = operator.apply(input);

        assertEquals(expected, actual);
    }
}

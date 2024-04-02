package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RenameTest {

    @Test
    public void simpleRenameSolutionMappingTest() {
        RenameOperator operator = new RenameOperator(Set.of(new Pair("?name", "?fullname")));
        SolutionMapping input = BlocksIO.readSolutionMapping("operators/rename/simpleRenameSolMap/input.json");
        SolutionMapping expected = BlocksIO.readSolutionMapping("operators/rename/simpleRenameSolMap/output.json");
        SolutionMapping actual = operator.applySolMapping(input);

        assertEquals(expected, actual);
    }

    @Test
    public void simpleTestMappingTuple() {
        RenameOperator operator = new RenameOperator(Set.of(new Pair("?name", "?fullname")));
        MappingTuple input = BlocksIO.readMappingTuple("operators/rename/simpleRenameMappingTuple/input.json");
        MappingTuple expected = BlocksIO.readMappingTuple("operators/rename/simpleRenameMappingTuple/output.json");
        MappingTuple actual = operator.applyMappingTuple(input);

        assertEquals(expected, actual);
    }
}

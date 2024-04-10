package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RenameTest {

    @Nested
    class PairRenameTests {
        RenameOperator operator = new RenameOperator(Set.of(new Pair("?name", "?fullname")));

        @Test
        public void simpleRenameSolutionMappingTest() {
            SolutionMapping input = BlocksIO.readSolutionMapping("operators/rename/simpleRenameSolMap/input.json");
            SolutionMapping expected = BlocksIO.readSolutionMapping("operators/rename/simpleRenameSolMap/output.json");
            SolutionMapping actual = operator.applySolMapping(input);

            assertEquals(expected, actual);
        }

        @Test
        public void simpleRenameMappingTuple() {
            MappingTuple input = BlocksIO.readMappingTuple("operators/rename/simpleRenameMappingTuple/input.json");
            MappingTuple expected = BlocksIO.readMappingTuple("operators/rename/simpleRenameMappingTuple/output.json");
            MappingTuple actual = operator.applyMappingTuple(input);

            assertEquals(expected, actual);
        }
    }

    @Nested
    class AliasTests {
        @Test
        public void aliasSolutionMappingTest() {
            RenameOperator operator = new RenameOperator("aliased_");
            SolutionMapping input = BlocksIO.readSolutionMapping("operators/rename/aliasSolutionMapping/input.json");
            SolutionMapping expected = BlocksIO.readSolutionMapping("operators/rename/aliasSolutionMapping/output.json");
            SolutionMapping actual = operator.applySolMapping(input);

            assertEquals(expected, actual);
        }

        @Test
        public void aliasMappingTupleTest() {
            RenameOperator operator = new RenameOperator("aliased_");
            MappingTuple input = BlocksIO.readMappingTuple("operators/rename/aliasMappingTuple/input.json");
            MappingTuple expected = BlocksIO.readMappingTuple("operators/rename/aliasMappingTuple/output.json");
            MappingTuple actual = operator.applyMappingTuple(input);

            assertEquals(expected, actual);
        }
    }
}

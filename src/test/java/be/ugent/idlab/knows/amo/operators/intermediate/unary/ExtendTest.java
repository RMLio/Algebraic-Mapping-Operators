package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.functions.ExtendFunction;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExtendTest {
    // simple condition to concatenate first and last names
    ExtendOperator op = new ExtendOperator("ExtendOp", Set.of("f_default"), Set.of("f_default"),
            List.of(new Pair<String, ExtendFunction>("?full_name", (mapping -> {
                LiteralNode firstNode = (LiteralNode) mapping.get("?first_name");
                LiteralNode secondNode = (LiteralNode) mapping.get("?last_name");
                return List.of(new LiteralNode(firstNode.getValue() + " " + secondNode.getValue()));
            }))));

    @Test
    public void simpleTestSolMapping() {
        SolutionMapping input = BlocksIO.readSolutionMapping("operators/extend/solutionMap/input.json");
        SolutionMapping expected = BlocksIO.readSolutionMapping("operators/extend/solutionMap/output.json");
        SolutionMapping actual = this.op.apply(input);

        assertEquals(expected, actual);
    }

    @Test
    public void simpleTestMappingTuple() {
        MappingTuple input = BlocksIO.readMappingTuple("operators/extend/mappingTuple/input.json");
        MappingTuple expected = BlocksIO.readMappingTuple("operators/extend/mappingTuple/output.json");

        MappingTuple actual = this.op.apply(input);
        assertEquals(expected, actual);
    }
}

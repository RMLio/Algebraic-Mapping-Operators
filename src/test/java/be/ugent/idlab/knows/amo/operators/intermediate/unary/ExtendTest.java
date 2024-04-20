package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.ExtendFunction;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import org.apache.jena.graph.NodeFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExtendTest {
    // simple condition to concatenate first and last names
    ExtendOperator op = new ExtendOperator(List.of(new Pair<String, ExtendFunction>("?full_name", (mapping -> {
        String first = mapping.get("?first_name").getLiteralValue().toString();
        String last = mapping.get("?last_name").getLiteralValue().toString();

        return NodeFactory.createLiteral(first + " " + last, mapping.get("?first_name").getLiteralDatatype());
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

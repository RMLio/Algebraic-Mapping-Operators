package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.functions.TargetSink;
import be.ugent.idlab.knows.amo.operators.target.TargetOperator;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import org.apache.jena.graph.Node_Literal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TargetOperatorTest {

    @Test
    public void simpleTest() {
        TestSink sink = new TestSink();

        MappingTuple input = BlocksIO.readMappingTuple("operators/target/input.json");

        TargetOperator operator = new TargetOperator("f_target", "?foo", sink);
        operator.apply(input);
        assertEquals("bar", sink.output);
    }
}

/**
 * Dummy sink that reads out the value of key "?foo" and stores it in a String
 */
class TestSink implements TargetSink<RDFNode> {
    String output;

    @Override
    public void sink(RDFNode data) {
        this.output = (String) data.getValue();
    }
}
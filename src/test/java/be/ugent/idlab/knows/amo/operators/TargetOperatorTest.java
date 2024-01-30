package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.TargetSink;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TargetOperatorTest {

    @Test
    public void simpleTest() {
        TestSink sink = new TestSink();

        SolutionMapping solMapping = new SolutionMapping(Map.of("?foo", "bar"));
        MappingTuple tuple = new MappingTuple();
        tuple.addSolutionMap("f_target", solMapping);

        TargetOperator operator = new TargetOperator("f_target", sink);
        operator.apply(tuple);
        assertEquals(sink.output, "bar");
    }
}

/**
 * Dummy sink that reads out the value of key "?foo" and stores it in a String
 */
class TestSink implements TargetSink {
    String output;

    @Override
    public void sink(SolutionMapping mapping) {
        this.output = mapping.get("?foo").toString();
    }
}
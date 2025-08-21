package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.functions.TargetSink;
import be.ugent.idlab.knows.amo.operators.target.TargetOperator;
import be.ugent.idlab.knows.amo.operators.target.postprocessing.RMLFormatter;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TargetOperatorTest {

    @Test
    public void simpleTest() {
        TestSink sink = new TestSink();

        MappingTuple input = BlocksIO.readMappingTuple("operators/target/input.json");

        TargetOperator operator = new TargetOperator("targetOp", "f_target", "?foo", sink);
        operator.apply(input);
        assertEquals("bar", sink.output);
    }

    /**
     * Tests outputting in different supported
     */
    @Nested
    class OutputLanguageTest {
        /**
         * A Smoke test: NQ to NQ
         */
        @Test
        public void nQuads() {
            String value = "<http://example.com/10/Venus> <http://xmlns.com/foaf/0.1/name> \"Venus\" .\n";
            MappingTuple tuple = new MappingTuple();
            tuple.setSolutionMaps("default", new SolutionMapping(Map.of(TargetOperator.TARGET_VARIABLE, new LiteralNode(value))));

            RMLFormatter formatter = new RMLFormatter(Lang.NQUADS);
            TestSink sink = new TestSink();

            TargetOperator operator = new TargetOperator("targetOp", "default", TargetOperator.TARGET_VARIABLE, sink, formatter);
            operator.apply(tuple);

            assertEquals(value, sink.output);
        }

        @Test
        public void jsonLD() {
            String value = "<http://example.com/10/Venus> <http://xmlns.com/foaf/0.1/name> \"Venus\" .\n";
            MappingTuple tuple = new MappingTuple();
            tuple.setSolutionMaps("default", new SolutionMapping(Map.of(TargetOperator.TARGET_VARIABLE, new LiteralNode(value))));

            RMLFormatter formatter = new RMLFormatter(Lang.JSONLD);
            TestSink sink = new TestSink();

            TargetOperator operator = new TargetOperator("targetOp", "default", TargetOperator.TARGET_VARIABLE, sink, formatter);
            operator.apply(tuple);

            String expected = """
                    {
                        "@id": "http://example.com/10/Venus",
                        "http://xmlns.com/foaf/0.1/name": "Venus"
                    }
                    """;
            assertEquals(expected, sink.output);
        }
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
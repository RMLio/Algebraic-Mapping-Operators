package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.functions.ExtendFunction;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A variable extended with a function that produces several values stands for several
 * records: the rule using it has to be applied to every value, not just the first.
 */
public class ExtendMultiValueTest {

    private static final String FRAGMENT = "default";

    /** Splits the record's {@code scope} on spaces, the way grel:string_split does. */
    private static final ExtendFunction SPLIT_SCOPE = new ExtendFunction() {
        @Override
        public String apply(SolutionMapping mapping) {
            List<String> values = this.applyMulti(mapping);
            return values.isEmpty() ? null : values.get(0);
        }

        @Override
        public List<String> applyMulti(SolutionMapping mapping) {
            if (mapping == null || mapping.get("scope") == null) {
                return List.of();
            }
            return Arrays.asList(mapping.get("scope").getValue().toString().split(" "));
        }

        @Override
        public List<RDFNode> applyMultiToNode(SolutionMapping mapping) {
            return this.applyMulti(mapping).stream().map(v -> (RDFNode) new LiteralNode(v)).toList();
        }
    };

    private static ExtendOperator operator(String variable, ExtendFunction function) {
        return new ExtendOperator("Extend", Set.of(FRAGMENT), Set.of(FRAGMENT),
                List.of(new Pair<>(variable, function)));
    }

    private static SolutionMapping record(String scope) {
        SolutionMapping mapping = new SolutionMapping();
        mapping.put("scope", new LiteralNode(scope));
        return mapping;
    }

    @Test
    public void aFunctionProducingSeveralValuesGivesAMappingPerValue() {
        List<SolutionMapping> extended = operator("?value", SPLIT_SCOPE).applyMulti(record("read write"));

        assertEquals(2, extended.size());
        assertEquals("read", extended.get(0).get("?value").getValue().toString());
        assertEquals("write", extended.get(1).get("?value").getValue().toString());
        // the record's own variables travel with every value
        assertTrue(extended.stream().allMatch(m -> m.get("scope") != null));
    }

    @Test
    public void aFunctionProducingOneValueGivesOneMapping() {
        List<SolutionMapping> extended = operator("?value", SPLIT_SCOPE).applyMulti(record("read"));

        assertEquals(1, extended.size());
        assertEquals("read", extended.get(0).get("?value").getValue().toString());
    }

    @Test
    public void aVariableThatStaysUnboundIsStillPut() {
        List<SolutionMapping> extended = operator("?value", SPLIT_SCOPE).applyMulti(new SolutionMapping());

        assertEquals(1, extended.size());
        assertTrue(extended.get(0).containsKey("?value"));
    }

    @Test
    public void everyValueReachesTheOutputTuple() {
        MappingTuple tuple = new MappingTuple();
        tuple.addSolutionMap(FRAGMENT, record("read write"));

        MappingTuple out = operator("?value", SPLIT_SCOPE).apply(tuple);

        Collection<SolutionMapping> mappings = out.getSolutionMappings(FRAGMENT);
        assertEquals(2, mappings.size());
    }
}

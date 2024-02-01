package be.ugent.idlab.knows.amo.blocks;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SolutionMappingTests {

    /**
     * A simple smoke test of applying a string to a mapping tuple
     */
    @Test
    public void simpleStringApply() {
        SolutionMapping mapping = new SolutionMapping(Map.of(
                "?age", 25,
                "?name", "John Doe"
        ));

        String s = "?name ; ?age";

        String expected = "John Doe ; 25";
        assertEquals(expected, mapping.apply(s));
    }

    @Test
    public void unionTest() {
        SolutionMapping m1 = new SolutionMapping(Map.of(
                "foo", 1
        ));

        SolutionMapping m2 = new SolutionMapping(Map.of(
                "bar", 2
        ));

        SolutionMapping merged = m1.union(m2);
        assertEquals(2, merged.keySet().size());
    }

    /**
     * An empty solution mapping is always compatible with another solution mapping.<br>
     * See <a href="https://w3c.github.io/sparql-query/spec/#sparqlAlgebra">SPARQL spec</a> and <a href="https://gitlab.ilabt.imec.be/rml/proc/algebraic-mapping-operators/-/issues/4">issue 4</a>
     */
    @Test
    public void compatibleSolutionMapping() {
        SolutionMapping m1 = new SolutionMapping(Map.of("foo", 1));
        SolutionMapping empty = new SolutionMapping(Map.of());

        assertTrue(m1.isCompatibleWith(empty));
        assertTrue(empty.isCompatibleWith(m1));
    }
}

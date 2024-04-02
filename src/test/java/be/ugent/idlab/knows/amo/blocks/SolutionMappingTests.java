package be.ugent.idlab.knows.amo.blocks;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.NodeFactory;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SolutionMappingTests {
    @Test
    public void unionTest() {
        SolutionMapping m1 = new SolutionMapping(Map.of(
                "foo", NodeFactory.createLiteral("1", XSDDatatype.XSDinteger)
        ));

        SolutionMapping m2 = new SolutionMapping(Map.of(
                "bar", NodeFactory.createLiteral("2", XSDDatatype.XSDinteger)
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
        SolutionMapping m1 = new SolutionMapping(Map.of("foo", NodeFactory.createLiteral("1", XSDDatatype.XSDinteger)));
        SolutionMapping empty = new SolutionMapping(Map.of());

        assertTrue(m1.isCompatibleWith(empty));
        assertTrue(empty.isCompatibleWith(m1));
    }
}

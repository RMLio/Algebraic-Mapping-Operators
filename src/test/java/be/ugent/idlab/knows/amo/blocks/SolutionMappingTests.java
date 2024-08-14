package be.ugent.idlab.knows.amo.blocks;

import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SolutionMappingTests {
    @Test
    public void unionTest() {
        SolutionMapping m1 = new SolutionMapping(Map.of(
                "foo", new LiteralNode("1", XSDDatatype.XSDinteger)
        ));

        SolutionMapping m2 = new SolutionMapping(Map.of(
                "bar", new LiteralNode("2", XSDDatatype.XSDinteger)
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
        SolutionMapping m1 = new SolutionMapping(Map.of("foo", new LiteralNode("1", XSDDatatype.XSDinteger)));
        SolutionMapping empty = new SolutionMapping(Map.of());

        assertTrue(m1.isCompatibleWith(empty));
        assertTrue(empty.isCompatibleWith(m1));
    }
}
//    @Test
//    public void foo() {
//        SolutionMapping mapping = new SolutionMapping();
//        mapping.put("foo", new LiteralNode("bar"));
//
//        RDFNode out = mapping.get("foo");
//
//        assertEquals("bar", mapping.get("foo").getValue());
//    }
//}

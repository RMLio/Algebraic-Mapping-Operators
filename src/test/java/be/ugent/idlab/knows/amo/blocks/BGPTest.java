package be.ugent.idlab.knows.amo.blocks;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.graph.GraphFactory;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BGPTest {

    @Test
    public void simpleTest() {
        // create a simple input graph consisting of a single triple
        Triple t = Triple.create(NodeFactory.createLiteral("?foo"), NodeFactory.createLiteral("foaf:name"), NodeFactory.createLiteral("?bar"));
        Graph graph = GraphFactory.createDefaultGraph();
        graph.add(t);

        // create a simple SolutionMapping
        SolutionMapping mapping = new SolutionMapping(Map.of(
                "?foo", "ex:JohnDoe",
                "?bar", "John"
        ));

        BGP bgp = new BGP(graph);

        Graph out = bgp.apply(mapping);

        assertEquals(1, out.size());
        Iterator<Triple> tripleIterator = out.find();
        assertTrue(tripleIterator.hasNext());
        Triple foundTriple = tripleIterator.next();
        assertFalse(tripleIterator.hasNext());

        assertEquals("ex:JohnDoe", foundTriple.getSubject().getLiteralValue().toString());
        assertEquals("John", foundTriple.getObject().getLiteralValue().toString());
    }
}

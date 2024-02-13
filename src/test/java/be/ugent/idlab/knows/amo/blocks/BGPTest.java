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
        Triple t = Triple.create(NodeFactory.createVariable("foo"), NodeFactory.createLiteral("foaf:name"), NodeFactory.createVariable("bar"));
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

    @Test
    public void readGraphFromString() {
        // graph as described in the paper Listing 2
        String graph = "?firstname_iri <http://example.com/name> ?fullname;\n" +
                "<http://example.com/petName> ?pet_name.";

        // SolutionMapping with values as described in Example 9
        SolutionMapping mapping = new SolutionMapping(Map.of(
                "?firstname_iri", "<http://example.com/John>",
                "?fullname", "John Doe",
                "?pet_name", "Max"
        ));

        BGP bgp = new BGP(graph);

        Graph out = bgp.apply(mapping);

        assertEquals(2, out.size());
        Iterator<Triple> iterator = out.find();
        assertTrue(iterator.hasNext());
        Triple t1 = iterator.next();
        Triple t2 = iterator.next();
        assertFalse(iterator.hasNext());

        // check variable replacement in first triple
        assertEquals("<http://example.com/John>", t1.getSubject().getLiteralValue().toString());
        assertEquals("John Doe", t1.getObject().getLiteralValue());

        // check variable replacement in second triple
        assertEquals("<http://example.com/John>", t2.getSubject().getLiteralValue().toString());
        assertEquals("Max", t2.getObject().getLiteralValue().toString());
    }
}

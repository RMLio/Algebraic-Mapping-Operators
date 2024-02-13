package be.ugent.idlab.knows.amo.blocks;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
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

        Model outModel = bgp.apply(mapping);

        Graph out = outModel.getGraph();

        assertEquals(1, out.size());
        Iterator<Triple> tripleIterator = out.find();
        assertTrue(tripleIterator.hasNext());
        Triple foundTriple = tripleIterator.next();
        assertFalse(tripleIterator.hasNext());

        assertEquals("ex:JohnDoe", foundTriple.getSubject().getURI());
        assertEquals("John", foundTriple.getObject().getLiteralValue().toString());
    }

    @Test
    public void readGraphFromString() {
        // graph as described in the paper Listing 2
        String graph = "?firstname_iri <http://example.com/name> ?fullname;\n" +
                "<http://example.com/petName> ?pet_name.";

        // SolutionMapping with values as described in Example 9
        SolutionMapping mapping = new SolutionMapping(Map.of(
                "?firstname_iri", "http://example.com/John",
                "?fullname", "John Doe",
                "?pet_name", "Max"
        ));

        BGP bgp = new BGP(graph);

        Model modelOut = bgp.apply(mapping);
        Graph out = modelOut.getGraph();

        assertEquals(2, out.size());
        Triple t1 = out.find(Node.ANY, NodeFactory.createURI("http://example.com/name"), Node.ANY).next();
        Triple t2 = out.find(Node.ANY, NodeFactory.createURI("http://example.com/petName"), Node.ANY).next();

        // check variable replacement in first triple
        assertEquals("http://example.com/John", t1.getSubject().getURI());
        assertEquals("John Doe", t1.getObject().getLiteralValue());

        // check variable replacement in second triple
        assertEquals("http://example.com/John", t2.getSubject().getURI());
        assertEquals("Max", t2.getObject().getLiteralValue().toString());
    }

    @Test
    public void tripleVariableTest(){
        Triple t = Triple.create(
                NodeFactory.createVariable("foo"),
                NodeFactory.createURI("foaf:name"),
                NodeFactory.createLiteral("bar")
        );

        assertEquals("foo", t.getSubject().getName());
        assertEquals("foo", t.getSubject().getName().toString());
    }
}

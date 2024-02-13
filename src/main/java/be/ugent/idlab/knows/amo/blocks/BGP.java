package be.ugent.idlab.knows.amo.blocks;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.graph.GraphFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Basic Graph Pattern is a pattern to generate RDF triples by replacement of variables.
 */
public class BGP {
    private final List<Triple> triples = new ArrayList<>();
    private final List<Integer> subjectVariables = new ArrayList<>();
    private final List<Integer> objectVariables = new ArrayList<>();

    /**
     * Constructor for reading a BGP in from a String.
     * @param pattern the BGP pattern
     */
    public BGP(String pattern) {
        // turn into a CONSTRUCT query on the fly
        pattern = "CONSTRUCT { " + pattern + "} WHERE { }";
        // create a new Query object
        Query query = QueryFactory.create(pattern);
        // fetch triples
        List<Triple> triples = query.getConstructTemplate().getTriples();
        // feed the triples into the graph and bootstrap the class
        Graph graph = ModelFactory.createDefaultModel().getGraph();
        for (Triple t : triples) {
            graph.add(t);
        }

        this.bootstrap(graph);
    }

    public BGP(Graph bgpGraph) {
        this.bootstrap(bgpGraph);
    }

    private void bootstrap(Graph bgpGraph) {
        // analyze the graph for variables
        Iterator<Triple> tripleIterator = bgpGraph.stream().iterator();
        int counter = 0;
        while (tripleIterator.hasNext()) {
            Triple t = tripleIterator.next();
            this.triples.add(t);

            // variables can only show up in the subject or the object part of the triple
            if (t.getSubject().isVariable()) {
                this.subjectVariables.add(counter);
            }

            if (t.getObject().isVariable()) {
                this.objectVariables.add(counter);
            }
            counter++;
        }
    }

    public Graph apply(SolutionMapping m) {
        // replace all known variables
        for (int index : this.subjectVariables) {
            Triple t = this.triples.get(index);

            String variable = "?" + t.getSubject().getName();

            if (m.containsKey(variable)) {
                String value = m.get(variable).toString();
                Triple newT = Triple.create(NodeFactory.createLiteral(value), t.getPredicate(), t.getObject());
                this.triples.set(index, newT);
            }
        }

        for (int index : this.objectVariables) {
            Triple t = this.triples.get(index);
            String variable = "?" + t.getObject().getName();
            if (m.containsKey(variable)) {
                String value = m.get(variable).toString();
                Triple newT = Triple.create(t.getSubject(), t.getPredicate(), NodeFactory.createLiteral(value));
                this.triples.set(index, newT);
            }
        }

        // construct a new graph
        Graph graph = GraphFactory.createDefaultGraph();
        for (Triple t : this.triples) {
            graph.add(t);
        }

        return graph;
    }
}

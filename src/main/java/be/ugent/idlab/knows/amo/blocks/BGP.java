package be.ugent.idlab.knows.amo.blocks;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.graph.GraphFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic Graph Pattern is a pattern to generate RDF triples by replacement of variables.
 */
public class BGP {
    private final List<Triple> triples;
    private final List<Integer> subjectVariables = new ArrayList<>();
    private final List<Integer> predicateVariables = new ArrayList<>();
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
        this.triples = query.getConstructTemplate().getTriples();

        this.bootstrap();
    }

    public BGP(Graph bgpGraph) {
        this.triples = bgpGraph.find().toList();
        this.bootstrap();
    }

    private void bootstrap() {
        // analyze the graph for variables
        for (int i = 0; i < triples.size(); i++) {
            Triple t = this.triples.get(i);
            // variables can only show up in the subject or the object part of the triple
            if (t.getSubject().isVariable()) {
                this.subjectVariables.add(i);
            }

            if (t.getPredicate().isVariable()) {
                this.predicateVariables.add(i);
            }

            if (t.getObject().isVariable()) {
                this.objectVariables.add(i);
            }
        }
    }

    public Model apply(SolutionMapping m) {
        // replace all known variables
        for (int index : this.subjectVariables) {
            Triple t = this.triples.get(index);

            String variable = "?" + t.getSubject().getName();

            if (m.containsKey(variable)) {
                String value = m.get(variable).toString();
                Triple newT = Triple.create(NodeFactory.createURI(value), t.getPredicate(), t.getObject());
                this.triples.set(index, newT);
            }
        }

        for (int index : this.predicateVariables) {
            Triple t = this.triples.get(index);

            String variable = "?" + t.getPredicate().getName();

            if (m.containsKey(variable)) {
                String value = m.get(variable).toString();
                Triple newT = Triple.create(t.getSubject(), NodeFactory.createURI(value), t.getObject());
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

        return ModelFactory.createModelForGraph(graph);
    }
}

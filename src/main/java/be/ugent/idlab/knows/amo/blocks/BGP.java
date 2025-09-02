package be.ugent.idlab.knows.amo.blocks;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.core.Quad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic Graph Pattern is a pattern to generate RDF triples by replacement of
 * variables.
 */
public class BGP implements Serializable {
    private static final String DEFAULT_GRAPH_NAME = "http://www.w3.org/ns/r2rml#defaultGraph";
    private final List<Quad> quads;
    private final List<Integer> subjectVariables = new ArrayList<>();
    private final List<Integer> predicateVariables = new ArrayList<>();
    private final List<Integer> objectVariables = new ArrayList<>();
    private final List<Integer> graphVariables = new ArrayList<>();

    /**
     * Constructor for reading a BGP in from a String.
     *
     * @param pattern the BGP pattern
     */
    public BGP(String pattern) {
        // turn the pattern into a list of quads
        List<String> lines = pattern.lines().toList();

        StringBuilder start = new StringBuilder("CONSTRUCT { ");
        String end = " } WHERE { }";

        for (String line : lines) {
            String[] parts = line.split(" ");
            if (parts.length == 4) { // a triple
                line = preprocessParts(parts);
                start.append(line);
            } else if (parts.length == 5) { // a quad that contains a graph
                // second to last part is the graph
                String graph = parts[3];
                String input = String.format("GRAPH %s { %s %s %s } .", graph, parts[0], parts[1], parts[2]);
                start.append(input);
            }
        }

        Query query = QueryFactory.create(start + end);
        this.quads = query.getConstructTemplate().getQuads();
        this.bootstrap();
    }

    /**
     * Preprocess the line: put quotes around string preceding a language tag
     *
     * @param parts Parts of the string originally separated by spaces.
     * @return Escaped language tags
     */
    private String preprocessParts(String[] parts) {
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.length() >= 4) {
                // escape the language tag: ?ob@en should become "?ob"@en
                if (part.charAt(part.length() - 3) == '@') {
                    int atIndex = part.indexOf('@');
                    part = "\"" + part.substring(0, atIndex) + "\"" + part.substring(atIndex);
                }
            }
            parts[i] = part;
        }
        return String.join(" ", parts);
    }

    private void bootstrap() {
        // analyze the graph for variables
        for (int i = 0; i < quads.size(); i++) {
            Quad q = this.quads.get(i);

            // either a variable or a literal with a variable inside
            if (q.getSubject().isVariable() ||
                    (q.getSubject().isLiteral() && q.getSubject().getLiteralValue().toString().startsWith("?"))) {
                this.subjectVariables.add(i);
            }

            if (q.getPredicate().isVariable()) {
                this.predicateVariables.add(i);
            }

            // either a variable or a literal with a variable inside
            if (q.getObject().isVariable() ||
                    (q.getObject().isLiteral() && q.getObject().getLiteralValue().toString().startsWith("?"))) {
                this.objectVariables.add(i);
            }

            if (q.getGraph().isVariable()) {
                this.graphVariables.add(i);
            }
        }
    }

    // TODO: Remove warning supression when ready to handle nullness <12-09-24, Min Oo> //

    /**
     * Replace all known variables
     * @param m The solution mappings to get the values of the variables from
     * @return  A dataset where all BGPs are replaced with known values.
     */
    @SuppressWarnings("null")
    public DatasetGraph apply(SolutionMapping m) {
        // replace all known variables

        List<Quad> newQuads = new ArrayList<>(this.quads);

        for (int index : this.subjectVariables) {
            Quad q = newQuads.get(index);

            String variable;
            if (q.getSubject().isVariable()) {
                variable = q.getSubject().toString();
                if (m.containsKey(variable) && m.get(variable) != null) {
                    q = Quad.create(q.getGraph(), m.get(variable).getJenaNode(), q.getPredicate(), q.getObject());
                    newQuads.set(index, q);
                }
            } else {
                Node_Literal n = (Node_Literal) q.getSubject();
                variable = n.getLiteralValue().toString();
                // n.getLiteral().getLexicalForm()
                String value = m.get(variable).toString();
                Node newNode = NodeFactory.createLiteral(value, n.getLiteralLanguage());
                // q.getsubject() m.get(variable).getJenaNode();
                Quad newQ = Quad.create(q.getGraph(), newNode, q.getPredicate(), q.getObject());
                newQuads.set(index, newQ);
            }
        }

        for (int index : this.predicateVariables) {
            Quad t = newQuads.get(index);

            String variable = t.getPredicate().toString();

            if (m.containsKey(variable)) {
                Quad newQ = Quad.create(t.getGraph(), t.getSubject(), m.get(variable).getJenaNode(), t.getObject());
                newQuads.set(index, newQ);
            }
        }

        for (int index : this.objectVariables) {
            Quad t = newQuads.get(index);
            String variable;
            if (t.getObject().isVariable()) {
                variable = t.getObject().toString();
            } else {
                variable = t.getObject().getLiteralValue().toString();
            }
            if (m.containsKey(variable)) {
                Quad newQ = Quad.create(t.getGraph(), t.getSubject(), t.getPredicate(), m.get(variable).getJenaNode());
                newQuads.set(index, newQ);
            }
        }

        for (int index : this.graphVariables) {
            Quad t = newQuads.get(index);
            String variable = t.getGraph().toString();
            if (m.containsKey(variable)) {
                Quad newQ = Quad.create(m.get(variable).getJenaNode(), t.getSubject(), t.getPredicate(), t.getObject());
                newQuads.set(index, newQ);
            }
        }

        // // construct a new graph
        DatasetGraph g = DatasetGraphFactory.create();
        for (Quad quad : newQuads) {
            String value;
            if (quad.getGraph().isLiteral()) {
                value = quad.getGraph().getLiteralValue().toString();
            } else if (quad.getGraph().isURI()) {
                value = quad.getGraph().getURI();
            } else {
                value = quad.getGraph().getBlankNodeLabel();
            }

            if (value.equals(BGP.DEFAULT_GRAPH_NAME)) {
                g.getDefaultGraph().add(quad.getSubject(), quad.getPredicate(), quad.getObject());
            } else {
                g.add(quad);
            }
        }

        return g;
    }
}

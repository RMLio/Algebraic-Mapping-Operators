package be.ugent.idlab.knows.amo.blocks;

import java.util.Collection;

/**
 * Basic Graph Pattern is a pattern to generate RDF triples by replacement of variables.
 */
public class BGP {

    private String pattern;
    private Collection<String> variables;

    public BGP(String pattern, Collection<String> variables) {
        this.pattern = pattern;
        this.variables = variables;
    }

    public String apply(SolutionMapping m) {
        String out = pattern;
        for (String variable : this.variables) {
            if (m.containsKey(variable)) {
                out = out.replaceAll(variable, m.get(variable).toString());
            }
        }

        return out;
    }
}

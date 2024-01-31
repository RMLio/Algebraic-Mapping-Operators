package be.ugent.idlab.knows.amo.blocks;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Basic Graph Pattern is a pattern to generate RDF triples by replacement of variables.
 */
public class BGP {

    private final String pattern;
    private final Collection<String> variables;

    public BGP(String pattern, Collection<String> variables) {
        this.pattern = pattern;
        this.variables = variables;
    }

    public String apply(SolutionMapping m) {
        String out = pattern;
        for (String variable : this.variables) {
            if (m.containsKey(variable)) {
                out = out.replaceAll(Pattern.quote(variable), m.get(variable).toString());
            }
        }

        return out;
    }
}

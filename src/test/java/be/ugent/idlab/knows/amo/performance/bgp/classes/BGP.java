package be.ugent.idlab.knows.amo.performance.bgp.classes;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Basic Graph Pattern is a pattern to generate RDF triples by replacement of variables.\
 * Baseline implementation
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

    public String applyWithIndex(SolutionMapping m) {
        String out = pattern;
        for (String variable : this.variables) {
            if (m.containsKey(variable)) {
                int index = out.indexOf(variable);
                while (index != -1) {
                    out = out.replace(variable, m.get(variable).toString());
                    index = out.indexOf(variable);
                }
            }
        }

        return out;
    }

    public String applyWithStringUtilsEach(SolutionMapping m) {
        String[] search = new String[m.size()];
        String[] replacement = new String[m.size()];

        int i = 0;
        for (String key : m.keySet()) {
            search[i] = key;
            replacement[i] = m.get(key).toString();
            i++;
        }


        return StringUtils.replaceEachRepeatedly(pattern, search, replacement);
    }

    public String applyWithStringUtilsReplace(SolutionMapping m) {
        String out = pattern;
        for (String variable: this.variables) {
            if (m.containsKey(variable)) {
                int index = StringUtils.indexOf(out, variable);
                while (index != -1) {
                    out = StringUtils.replace(out, variable, m.get(variable).toString());
                    index = StringUtils.indexOf(out, variable);
                }
            }
        }

        return out;
    }
}

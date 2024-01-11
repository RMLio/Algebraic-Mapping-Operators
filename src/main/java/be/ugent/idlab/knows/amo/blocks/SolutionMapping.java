package be.ugent.idlab.knows.amo.blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Solution mapping is a collection of key-value pairs connecting variables to their data
 * These can be applied on a templated string
 */
public class SolutionMapping { // leaning on Java implementation, subject to change
    protected final Map<String, Object> variables;

    public SolutionMapping() {
        this.variables = new HashMap<>();
    }

    public SolutionMapping(Map<String, Object> variables) {
        this.variables = variables;
    }

    public String apply(String templateString) {
        for(Map.Entry<String, Object> entry : variables.entrySet()) {
            if (templateString.contains(entry.getKey())) {
                templateString = templateString.replace(entry.getKey(), entry.getValue().toString());
            }
        }

        return templateString;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolutionMapping that = (SolutionMapping) o;
        return Objects.equals(variables, that.variables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variables);
    }
}

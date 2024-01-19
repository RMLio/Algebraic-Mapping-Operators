package be.ugent.idlab.knows.amo.blocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Solution mapping is a collection of key-value pairs connecting variables to their data
 * These can be applied on a templated string
 */
public class SolutionMapping extends HashMap<String, Object> {
    public SolutionMapping() {
        super();
    }
    public SolutionMapping(Map<String, Object> variables) {
        super(variables);
    }

    public String apply(String templateString) {
        for (Map.Entry<String, Object> entry : this.entrySet()) {
            if (templateString.contains(entry.getKey())) {
                templateString = templateString.replace(entry.getKey(), entry.getValue().toString());
            }
        }

        return templateString;
    }

    /**
     * @param that
     */
    public SolutionMapping union(SolutionMapping that) {
        // return an empty solution map if not compatible
        if (!this.isCompatibleWith(that)) {
            return new SolutionMapping();
        }

        SolutionMapping sol = new SolutionMapping(this);

        for (String key : that.keySet()) {
            if (!this.containsKey(key)) {
                sol.put(key, that.get(key));
            }
        }

        return sol;
    }

    /**
     * Checks compatibility with another SolutionMapping
     * <p>
     * Two SolutionMappings are compatible if values of all common variables are the same
     *
     * @param that SolutionMapping to check compatibility with
     * @return true if the SolutionMappings are compatible, false otherwise
     */
    public boolean isCompatibleWith(SolutionMapping that) {
        // find all common keys
        Set<String> uniqueKeys = new HashSet<>(this.keySet());
        uniqueKeys.retainAll(that.keySet());

        if (uniqueKeys.isEmpty()) {
            return false;
        }

        // verify the values of all common keys
        for (String key : uniqueKeys) {
            if (!this.get(key).equals(that.get(key))) {
                return false;
            }
        }
        return true;
    }
}

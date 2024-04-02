package be.ugent.idlab.knows.amo.blocks;

import org.apache.jena.graph.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Solution mapping is a collection of key-value pairs connecting variables to their data
 * These can be applied on a templated string
 */
public class SolutionMapping extends HashMap<String, Node> {
    public SolutionMapping() {
        super();
    }

    public SolutionMapping(Map<String, Node> variables) {
        super(variables);
    }

    /**
     * @param that
     */
    public SolutionMapping union(SolutionMapping that) {
        SolutionMapping sol = new SolutionMapping(this);
        sol.putAll(that);
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
        if (this.isEmpty() || that.isEmpty()) {
            return true;
        }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof SolutionMapping that) {
            for (Map.Entry<String, Node> e : this.entrySet()) {
                if (! that.get(e.getKey()).getLiteralValue().equals(e.getValue().getLiteralValue())) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

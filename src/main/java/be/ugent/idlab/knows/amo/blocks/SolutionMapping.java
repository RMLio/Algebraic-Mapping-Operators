package be.ugent.idlab.knows.amo.blocks;

import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Solution mapping is a collection of key-value pairs connecting variables to their data
 * These can be applied on a templated string
 */
public class SolutionMapping extends HashMap<String, RDFNode> {
    /**
     * A copy constructor
     *
     * @param mapping SolutionMapping to be copied
     */
    public SolutionMapping(SolutionMapping mapping) {
        this.putAll(mapping);
    }

    public SolutionMapping() {
        super();
    }

    public SolutionMapping(Map<String, RDFNode> variables) {
        this.putAll(variables);
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
        Set<String> commonKeys = new HashSet<>(this.keySet());
        commonKeys.retainAll(that.keySet());

        if (commonKeys.isEmpty()) { // if there are no common keys, mappings are compatible
            return true;
        }

        // verify the values of all common keys
        for (String key : commonKeys) {
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
            for (Map.Entry<String, RDFNode> e : this.entrySet()) {
                RDFNode n = that.get(e.getKey());
                if (!e.getValue().equals(n)) {
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

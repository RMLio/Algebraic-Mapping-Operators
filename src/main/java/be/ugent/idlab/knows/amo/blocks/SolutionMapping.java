package be.ugent.idlab.knows.amo.blocks;

import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import net.minidev.json.JSONObject;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Solution mapping is a collection of key-value pairs connecting variables to
 * their data
 * These can be applied on a templated string
 */
public class SolutionMapping extends HashMap<String, @Nullable RDFNode> implements Serializable {

    public SolutionMapping() {
        super();
    }

    public SolutionMapping(@NonNull SolutionMapping solutionMapping) {
        super();
        this.putAll(solutionMapping);
    }

    public SolutionMapping(@NonNull Map<String, @Nullable RDFNode> solutionMapping) {
        this.putAll(solutionMapping);
    }

    /**
     * @param that The other solution mapping
     */
    public SolutionMapping union(@Nullable SolutionMapping that) {
        if (that == null) {
            return new SolutionMapping(this);
        }
        SolutionMapping sol = new SolutionMapping(this);
        sol.putAll(that);
        return sol;
    }

    /**
     * Checks compatibility with another SolutionMapping
     * <p>
     * Two SolutionMappings are compatible if values of all common variables are the
     * same
     *
     * @param that SolutionMapping to check compatibility with
     * @return true if the SolutionMappings are compatible, false otherwise
     */
    public boolean isCompatibleWith(@Nullable SolutionMapping that) {
        if (that == null) {
            return true;
        }

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
            RDFNode thisValue = this.get(key);
            RDFNode thatValue = that.get(key);

            if (thisValue != null && thatValue != null) {
                if (!thisValue.equals(thatValue)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof SolutionMapping that) {
            for (Map.Entry<String, RDFNode> entry : this.entrySet()) {
                RDFNode thatValue = that.get(entry.getKey());
                RDFNode thisValue = entry.getValue();
                if (thisValue != null && !thisValue.equals(thatValue)) {
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

    @Override
    public String toString() {
        return new JSONObject(this).toString();
    }
}

package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.OperatorVisitor;

import java.util.Collection;

/**
 * RenameOperator will rename the variables.
 *
 * @param pairs a collection of Pairs to rename the variables. The pairs are supplied as instances of Pair(var_to_rename, new_var_name).
 */
public class RenameOperator implements UnaryOperator {

    private final RenameOperatorImpl implementation;

    public RenameOperator(Collection<Pair<String, String>> pairs) {
        this.implementation = new RenameOperatorPairs(pairs);
    }

    public RenameOperator(String alias) {
        this.implementation = new RenameOperatorAlias(alias);
    }

    @Override
    public <T> T accept(OperatorVisitor<T> visitor) {
        return null;
    }

    @Override
    public SolutionMapping apply(SolutionMapping mapping) {
        return this.implementation.apply(mapping);
    }

    @Override
    public MappingTuple apply(MappingTuple tuple) {
        return this.implementation.apply(tuple);
    }

    /**
     * Marker interface for the private implementations
     */
    interface RenameOperatorImpl extends UnaryOperator {
        @Override
        default <T> T accept(OperatorVisitor<T> visitor) {
            return null;
        }
    }

    static class RenameOperatorPairs implements RenameOperatorImpl {

        private final Collection<Pair<String, String>> pairs;

        public RenameOperatorPairs(Collection<Pair<String, String>> pairs) {
            this.pairs = pairs;
        }

        @Override
        public SolutionMapping apply(SolutionMapping mapping) {
            SolutionMapping out = new SolutionMapping();
            for (Pair<String, String> p : this.pairs) {
                for (String key : mapping.keySet()) {
                    if (key.equals(p.first())) {
                        out.put(p.second(), mapping.get(key));
                    } else {
                        out.put(key, mapping.get(key));
                    }
                }
            }

            return out;
        }
    }

    static class RenameOperatorAlias implements RenameOperatorImpl {
        private final String alias;

        public RenameOperatorAlias(String alias) {
            this.alias = alias;
        }

        @Override
        public SolutionMapping apply(SolutionMapping mapping) {
            SolutionMapping out = new SolutionMapping();
            for (String key : mapping.keySet()) {
                String aliased = "?" + alias + key.substring(1);
                out.put(aliased, mapping.get(key));
            }

            return out;
        }
    }
}

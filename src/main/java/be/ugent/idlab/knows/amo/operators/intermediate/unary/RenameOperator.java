package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

import java.io.ObjectInputStream;
import java.io.Serial;
import java.util.Collection;

/**
 * RenameOperator will rename the variables.
 *
 * @param pairs a collection of Pairs to rename the variables. The pairs are supplied as instances of Pair(var_to_rename, new_var_name).
 */
public class RenameOperator extends UnaryOperator {

    private final RenameOperatorImpl implementation;

    public RenameOperator(String operatorName, String fragment, Collection<Pair<String, String>> pairs) {
        super(operatorName, fragment);
        this.implementation = new RenameOperatorPairs(operatorName, fragment, pairs);
    }

    public RenameOperator(String operatorName, String fragment, String alias) {
        super(operatorName, fragment);
        this.implementation = new RenameOperatorAlias(operatorName, fragment, alias);
    }

    @Serial
    private void readObject(ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
        this.bootstrap();
    }

    private void bootstrap() {

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
    abstract static class RenameOperatorImpl extends UnaryOperator {
        /**
         * @param fragment fragment the operator should operate on
         */
        public RenameOperatorImpl(String operatorName, String fragment) {
            super(operatorName, fragment);
        }
    }

    static class RenameOperatorPairs extends RenameOperatorImpl {

        private final Collection<Pair<String, String>> pairs;

        public RenameOperatorPairs(String operatorName, String fragment, Collection<Pair<String, String>> pairs) {
            super(operatorName, fragment);
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

    static class RenameOperatorAlias extends RenameOperatorImpl {
        private final String alias;

        public RenameOperatorAlias(String operatorName, String fragment, String alias) {
            super(operatorName, fragment);
            this.alias = alias;
        }

        @Override
        public SolutionMapping apply(SolutionMapping mapping) {
            SolutionMapping out = new SolutionMapping();
            for (String key : mapping.keySet()) {
                String aliased = "?" + alias + key; // TODO: here stood key.substring(1), this might mess with the tests
                out.put(aliased, mapping.get(key));
            }

            return out;
        }
    }
}

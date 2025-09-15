package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;

import java.io.ObjectInputStream;
import java.io.Serial;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;

/**
 * RenameOperator will rename the variables.
 */
public class RenameOperator extends UnaryOperator {

    private final RenameOperatorImpl implementation;

    /**
     * Creates a new instance of a RenameOperator with a given name, fragment and rename-pairs.
     *
     * @param operatorName  A name (identifier) for the operator.
     * @param inputFragments    The input fragments of the operator.
     * @param outputFragments   The output fragments of the operator.
     * @param pairs         a collection of Pairs to rename the variables. The pairs are
     *                      supplied as instances of Pair(var_to_rename, new_var_name).
     */
    public RenameOperator(String operatorName, Set<String> inputFragments, Set<String> outputFragments, Collection<Pair<String, String>> pairs) {
        super(operatorName, inputFragments, outputFragments);
        this.implementation = new RenameOperatorPairs(operatorName, inputFragments, outputFragments, pairs);
    }

    /**
     * Creates a new instance of a RenameOperator with a given name, fragment and rename alias.
     *
     * @param operatorName  A name (identifier) for the operator.
     * @param inputFragments    The input fragments of the operator.
     * @param outputFragments   The output fragments of the operator.
     * @param alias         Every key (variable) is prefixed with alias + '.'
     */
    public RenameOperator(String operatorName, Set<String> inputFragments, Set<String> outputFragments, String alias) {
        super(operatorName, inputFragments, outputFragments);
        this.implementation = new RenameOperatorAlias(operatorName, inputFragments, outputFragments, alias);
    }

    @Serial
    private void readObject(ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
        this.bootstrap();
    }

    private void bootstrap() {

    }

    @Override
    @Nullable
    public SolutionMapping apply(@Nullable SolutionMapping mapping) {
        return this.implementation.apply(mapping);
    }

    @Override
    public MappingTuple apply(@Nullable MappingTuple tuple) {
        return this.implementation.apply(tuple);
    }

    /**
     * Marker interface for the private implementations
     */
    abstract static class RenameOperatorImpl extends UnaryOperator {
        /**
         * Instantiates a class doing a rename operation.
         *
         * @param operatorName      A name (identifier) for the operator.
         * @param inputFragments    The input fragments of the operator.
         * @param outputFragments   The output fragments of the operator.
         */
        public RenameOperatorImpl(String operatorName, Set<String> inputFragments, Set<String> outputFragments) {
            super(operatorName, inputFragments, outputFragments);
        }
    }

    static class RenameOperatorPairs extends RenameOperatorImpl {

        private final Collection<Pair<String, String>> pairs;

        /**
         * Instantiates a RenameOperator using rename-pairs.
         * @param operatorName      A name (identifier) for the operator.
         * @param inputFragments    The input fragments of the operator.
         * @param outputFragments   The output fragments of the operator.
         * @param pairs             A collection of rename-pairs. The first element in a pair is the old name of the variable,
         *                          the second element is the new name.
         *                          TODO: use a map old -> new instead?
         */
        public RenameOperatorPairs(String operatorName, Set<String> inputFragments, Set<String> outputFragments, Collection<Pair<String, String>> pairs) {
            super(operatorName, inputFragments, outputFragments);
            this.pairs = pairs;
        }

        @Override
        @Nullable
        public SolutionMapping apply(@Nullable SolutionMapping mapping) {
            if (mapping == null) {
                return null;
            }

            SolutionMapping out = new SolutionMapping();
            for (Pair<String, String> p : this.pairs) {
                for (Map.Entry<String, RDFNode> entry : mapping.entrySet()) {
                    String key = entry.getKey();
                    RDFNode value = entry.getValue();
                    if (key.equals(p.first())) {
                        out.put(p.second(), value);
                    } else {
                        out.put(key, value);
                    }
                }
            }

            return out;
        }
    }

    static class RenameOperatorAlias extends RenameOperatorImpl {
        private final String alias;

        /**
         * nstantiates a RenameOperator prefixing variable names with an "alias".
         * @param operatorName      A name (identifier) for the operator.
         * @param inputFragments    The input fragments of the operator.
         * @param outputFragments   The output fragments of the operator.
         * @param alias             Every key will be renamed to {@code alias + "." + key}.
         */
        public RenameOperatorAlias(String operatorName, Set<String> inputFragments, Set<String> outputFragments, String alias) {
            super(operatorName, inputFragments, outputFragments);
            this.alias = alias;
        }

        @Override
        @Nullable
        public SolutionMapping apply(@Nullable SolutionMapping mapping) {
            if (mapping == null) {
                return null;
            }
            SolutionMapping out = new SolutionMapping();
            for (Map.Entry<String, RDFNode> entry : mapping.entrySet()) {
                String key = entry.getKey();
                String aliased = alias + "." + key; // TODO: here stood key.substring(1), this might mess with the tests
                out.put(aliased, entry.getValue());
            }

            return out;
        }
    }
}

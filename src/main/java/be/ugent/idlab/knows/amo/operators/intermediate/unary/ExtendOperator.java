package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.functions.ExtendFunction;

import java.io.ObjectInputStream;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jspecify.annotations.Nullable;

/**
 * ExtendOperator will generate new variables (potentially from existing
 * variables) and add these to the SolutionMapping and / or MappingTuple.
 * This is done using the ExtendFunction provided
 */
public class ExtendOperator extends UnaryOperator {

    private final List<Pair<String, ExtendFunction>> replacements;

    /**
     * Instantiates a new "Extend" operator.
     * @param operatorName      The name (identifier) of the operator.
     * @param inputFragments    The input fragments of the operator.
     * @param outputFragments   The output fragments of the operator.
     * @param replacements      A collection of replacement pairs. The first element in the pair is the variable name to replace.
     *                          The second element is a function whose result is the replacement.
     */
    public ExtendOperator(String operatorName, Set<String> inputFragments, Set<String> outputFragments, Collection<Pair<String, ExtendFunction>> replacements) {
        super(operatorName, inputFragments, outputFragments);
        this.replacements = new ArrayList<>();
        this.replacements.addAll(replacements);
    }

    @Serial
    private void readObject(ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
    }

    @Override
    @Nullable
    public SolutionMapping apply(@Nullable SolutionMapping mapping) {
        if (mapping == null){
            return null;
        }

        List<SolutionMapping> extended = applyMulti(mapping);

        return extended.isEmpty() ? mapping : extended.get(0);
    }

    /**
     * Extends the mapping, giving a mapping per value the variables were extended with.
     * <p>
     * A function normally produces one value, and then one mapping comes back out. One
     * that produces several, a split for instance, stands for several records: the
     * mapping is repeated for each of its values, so that the rule using the variable is
     * applied to every one of them. Several such variables multiply out.
     *
     * @param mapping the mapping to extend
     * @return the extended mappings, never empty
     */
    public List<SolutionMapping> applyMulti(@Nullable SolutionMapping mapping) {
        if (mapping == null) {
            // a mapping that is not there stays not there, as it did before
            return Collections.singletonList(null);
        }

        List<SolutionMapping> extended = new ArrayList<>();
        extended.add(mapping);

        for (Pair<String, ExtendFunction> functionPair : this.replacements) {
            String variable = functionPair.first();
            List<SolutionMapping> next = new ArrayList<>();

            for (SolutionMapping current : extended) {
                if (current.containsKey(variable)) {
                    next.add(current);
                    continue;
                }

                List<RDFNode> nodes = functionPair.second().applyMultiToNode(current);
                if (nodes.size() <= 1) {
                    // a variable that stays unbound is still put, as it was before: the
                    // rules using it decide what an absent value means
                    current.put(variable, nodes.isEmpty() ? null : nodes.get(0));
                    next.add(current);
                    continue;
                }

                for (RDFNode node : nodes) {
                    SolutionMapping perValue = new SolutionMapping(current);
                    perValue.put(variable, node);
                    next.add(perValue);
                }
            }

            extended = next;
        }

        return extended;
    }

    @Override
    public MappingTuple apply(@Nullable MappingTuple tuple) {
        if (tuple == null){
            return null; 
        }

        MappingTuple out = new MappingTuple();

        for (String inputFragment : getInputFragments()) {
            Collection<SolutionMapping> mappings = tuple.getSolutionMappings(inputFragment);

            List<SolutionMapping> processedMappings = new ArrayList<>();

            // if there is no mappings to process, apply the function to an empty map, in case the replacements contain constant values
            if (mappings.isEmpty()) {
                processedMappings.addAll(this.applyMulti(new SolutionMapping()));
            } else {
                for (SolutionMapping m : mappings) {
                    processedMappings.addAll(applyMulti(m));
                }
            }

            for (String outputFragment : getOutputFragments()) {
                out.setSolutionMaps(outputFragment, processedMappings);
            }

        }

        return out;
    }
}

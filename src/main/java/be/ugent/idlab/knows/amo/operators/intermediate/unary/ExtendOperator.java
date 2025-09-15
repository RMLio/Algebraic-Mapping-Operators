package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.ExtendFunction;

import java.io.ObjectInputStream;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
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

        for (Pair<String, ExtendFunction> functionPair : this.replacements) {
            if (!mapping.containsKey(functionPair.first())) {
                mapping.put(functionPair.first(), functionPair.second().applyToNode(mapping));
            }
        }

        return mapping;
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
                processedMappings.add(this.apply(new SolutionMapping()));
            } else {
                for (SolutionMapping m : mappings) {
                    SolutionMapping processed = apply(m);
                    processedMappings.add(processed);
                }
            }

            for (String outputFragment : getOutputFragments()) {
                out.setSolutionMaps(outputFragment, processedMappings);
            }

        }

        return out;
    }
}

package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.ExtendFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ExtendOperator will generate new variables (potentially from existing variables) and add these to the SolutionMapping and / or MappingTuple.
 * This is done using the ExtendFunction provided
 *
 * @param variableName name of the variable to replace
 * @param function function to generate the new variable
 */
public record ExtendOperator(String variableName, ExtendFunction function) implements UnaryOperator {
    @Override
    public SolutionMapping applySolMapping(SolutionMapping mapping) {
        Object value = function.apply(mapping);
        mapping.put(variableName, value);

        return mapping;
    }

    @Override
    public MappingTuple applyMappingTuple(MappingTuple tuple) {
        for (String fragment : tuple.getFragments()) {
            Collection<SolutionMapping> mappings = tuple.getSolutionMappings(fragment);

            List<SolutionMapping> processedMappings = new ArrayList<>();
            for (SolutionMapping mapping : mappings) {
                applySolMapping(mapping);
                processedMappings.add(mapping);
            }

            tuple.setSolutionMap(fragment, processedMappings);
        }
        return tuple;
    }
}

package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

import java.util.Collection;
import java.util.Map;

/**
 * Project operator responsible for restricting the solution mappings.
 * All variables that are not contained in the supplied collection will be removed from the SolutionMapping / MappingTuple.
 *
 * @param variables a collection of variables to be kept
 */
public record ProjectOperator(Collection<String> variables) implements UnaryOperator {
    public SolutionMapping applySolMapping(SolutionMapping mapping) {
        for (String variable : this.variables) {
            ((Map<String, Object>) mapping).remove(variable);
        }

        return mapping;
    }

    public MappingTuple applyMappingTuple(MappingTuple tuple) {
        for (String fragment : tuple.getFragments()) {
            applySolMapping(tuple.getSolutionMappings(fragment));
        }

        return tuple;
    }
}

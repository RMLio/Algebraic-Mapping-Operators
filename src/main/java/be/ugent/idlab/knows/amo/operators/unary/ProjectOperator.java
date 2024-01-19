package be.ugent.idlab.knows.amo.operators.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

import java.util.List;
import java.util.Map;

/**
 * Project operator responsible for restricting the solution mappings.
 */
public record ProjectOperator(List<String> variables) implements UnaryOperators {
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

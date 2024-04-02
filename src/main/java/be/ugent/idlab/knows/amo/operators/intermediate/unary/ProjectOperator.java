package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import org.apache.jena.graph.Node;

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
        SolutionMapping newMapping = new SolutionMapping(mapping);
        for (Map.Entry<String, Node> entry : mapping.entrySet()) {
            if (!this.variables.contains(entry.getKey())) {
                newMapping.put(entry.getKey(), entry.getValue());
            }
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

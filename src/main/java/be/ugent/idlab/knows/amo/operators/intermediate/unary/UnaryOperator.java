package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Base interface for all operators that work on individual SolutionMappings and MappingTuples
 */
public interface UnaryOperator {

    /**
     * Apply the operator on a SolutionMapping
     *
     * @param mapping
     * @return the processed SolutionMapping
     */
    SolutionMapping applySolMapping(SolutionMapping mapping);

    /**
     * Apply the operator on the entire collection of SolutionMappings.
     *
     * @param mappings a Collection of SolutionMappings to apply the operator on
     * @return a collection of processed SolutionMappings (internally returned as a List)
     */
    default Collection<SolutionMapping> applySolMapping(Collection<SolutionMapping> mappings) {
        return mappings.stream()
                .map(this::applySolMapping)
                .collect(Collectors.toList());
    }

    /**
     * Apply the operator on a MappingTuple.
     *
     * @param tuple tuple to apply the operator on.
     * @return the processed MappingTuple
     */
    default MappingTuple applyMappingTuple(MappingTuple tuple) {
        MappingTuple out = new MappingTuple();

        for (String fragment : tuple.getFragments()) {
            for (SolutionMapping map : tuple.getSolutionMappings(fragment)) {
                SolutionMapping newMap = applySolMapping(map);
                out.addSolutionMap(fragment, newMap);
            }
        }

        return out;
    }

    /**
     * Apply the operator on the entire collection of MappingTuples.
     *
     * @param tuples a Collection of MappingTuples to apply the operator on.
     * @return a Collection of processed MappingTuples (internally returned as a List).
     */
    default Collection<MappingTuple> applyMappingTuple(Collection<MappingTuple> tuples) {
        return tuples.stream().map(this::applyMappingTuple).collect(Collectors.toList());
    }
}

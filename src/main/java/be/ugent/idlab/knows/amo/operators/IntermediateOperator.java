package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Base interface for all intermediate operators
 */

public interface IntermediateOperator {

    /**
     * @param mapping
     * @return
     */
    SolutionMapping applySolMapping(SolutionMapping mapping);

    default Collection<SolutionMapping> applySolMapping(Collection<SolutionMapping> mappings) {
        return mappings.stream()
                .map(this::applySolMapping)
                .collect(Collectors.toList());
    }

    MappingTuple applyMappingTuple(MappingTuple tuple);

    default Collection<MappingTuple> applyMappingTuple(Collection<MappingTuple> tuples) {
        return tuples.stream().map(this::applyMappingTuple).collect(Collectors.toList());
    }
}

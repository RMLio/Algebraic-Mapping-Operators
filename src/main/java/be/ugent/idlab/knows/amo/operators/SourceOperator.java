package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;

import java.util.Collection;

/**
 * Operator responsible for reading input into the plan.
 */
public interface SourceOperator extends Operator {

    /**
     * Generate a collection of MappingTuples from a particular source
     * @return a collection of MappingTuples
     */
    Collection<MappingTuple> getMappingTuples();
}

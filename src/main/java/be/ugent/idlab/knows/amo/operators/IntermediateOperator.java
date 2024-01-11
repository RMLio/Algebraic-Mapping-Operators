package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

/**
 * Base interface for all intermediate operators
 */

@FunctionalInterface
public interface IntermediateOperator {

    /**
     *
     * @param mapping
     * @return
     */
    public SolutionMapping apply(SolutionMapping mapping);
}

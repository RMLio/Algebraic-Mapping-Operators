package be.ugent.idlab.knows.amo.functions;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

/**
 * Function used as a condition for joining solution mappings.
 */
@FunctionalInterface
public interface JoinCondition {
    boolean apply(SolutionMapping s1, SolutionMapping s2);
}

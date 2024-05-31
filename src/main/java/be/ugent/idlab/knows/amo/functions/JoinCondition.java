package be.ugent.idlab.knows.amo.functions;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

import java.io.Serializable;

/**
 * Function used as a condition for joining solution mappings.
 */
@FunctionalInterface
public interface JoinCondition extends Serializable {
    boolean apply(SolutionMapping s1, SolutionMapping s2);
}

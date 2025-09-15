package be.ugent.idlab.knows.amo.operators.intermediate;

import be.ugent.idlab.knows.amo.operators.Operator;

import java.util.Set;


/**
 * Base class for all intermediate operators
 */
public abstract class IntermediateOperator extends Operator {

    /**
     * Creates a new instance of a IntermediateOperator
     * @param operatorName A name (identifier) for the operator.
     * @param inputFragments    The input fragments of the operator.
     * @param outputFragments   The output fragments of the operator.
     */
    protected IntermediateOperator(String operatorName, Set<String> inputFragments, Set<String> outputFragments) {
        super(operatorName, inputFragments, outputFragments);
    }
}

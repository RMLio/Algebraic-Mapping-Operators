package be.ugent.idlab.knows.amo.operators.intermediate;

import be.ugent.idlab.knows.amo.operators.Operator;


/**
 * Base class for all intermediate operators
 */
public abstract class IntermediateOperator extends Operator {

    protected final String fragment;

    /**
     * Creates a new instance of a IntermediateOperator
     * @param operatorName A name (identifier) for the operator.
     * @param fragment     The fragment this operator works on.
     */
    protected IntermediateOperator(String operatorName, String fragment) {
        super(operatorName);
        this.fragment = fragment;
    }

    /**
     * Gets the fragment of this operator.
     * @return  The name of the fragment this operator works on.
     */
    public String getFragment() {
        return fragment;
    }
}

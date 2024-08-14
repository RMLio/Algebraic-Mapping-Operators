package be.ugent.idlab.knows.amo.operators.intermediate;

import be.ugent.idlab.knows.amo.operators.Operator;

import java.io.Serializable;

/**
 * Base class for all intermediate operators
 */
public abstract class IntermediateOperator extends Operator implements Serializable {

    protected final String fragment;

    /**
     *
     * @param fragment fragment the operator should operate on
     */
    public IntermediateOperator(String operatorName, String fragment) {
        super(operatorName);
        this.fragment = fragment;
    }

    public String getFragment() {
        return fragment;
    }
}

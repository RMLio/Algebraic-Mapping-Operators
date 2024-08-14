package be.ugent.idlab.knows.amo.operators;

import java.io.Serializable;

/**
 * Marker interface for all operators
 */
public abstract class Operator implements Serializable {

    private final String operatorName;

    public Operator(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorName() {
        return this.operatorName;
    }

    public abstract <T> T accept(OperatorVisitor<T> visitor);

    @Override
    public String toString() {
        return this.operatorName;
    }
}

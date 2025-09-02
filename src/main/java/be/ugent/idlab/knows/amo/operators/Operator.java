package be.ugent.idlab.knows.amo.operators;

import java.io.Serializable;

import org.jspecify.annotations.NonNull;

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

    public abstract <T> T accept(@NonNull OperatorVisitor<@NonNull T> visitor);

    @Override
    public String toString() {
        return this.operatorName;
    }
}

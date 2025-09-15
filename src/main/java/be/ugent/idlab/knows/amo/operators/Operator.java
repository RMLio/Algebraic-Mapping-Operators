package be.ugent.idlab.knows.amo.operators;

import java.io.Serializable;
import java.util.Set;

import org.jspecify.annotations.NonNull;

/**
 * Marker interface for all operators
 */
public abstract class Operator implements Serializable {

    private final String operatorName;

    private final Set<String> inputFragments;
    private final Set<String> outputFragments;

    /**
     * Instantiates a new Operator.
     * @param operatorName      The name (identifier) of the operator.
     * @param inputFragments    The input fragments of the operator.
     * @param outputFragments   The output fragments of the operator.
     */
    public Operator(String operatorName,  Set<String> inputFragments, Set<String> outputFragments) {
        this.operatorName = operatorName;
        this.inputFragments = inputFragments;
        this.outputFragments = outputFragments;
    }

    public String getOperatorName() {
        return this.operatorName;
    }

    public Set<String> getInputFragments() {
        return inputFragments;
    }

    public Set<String> getOutputFragments() {
        return outputFragments;
    }

    public abstract <T> T accept(@NonNull OperatorVisitor<@NonNull T> visitor);

    @Override
    public String toString() {
        return "Operator{" +
                "operatorName='" + operatorName + '\'' +
                ", inputFragments=" + inputFragments +
                ", outputFragments=" + outputFragments +
                '}';
    }
}

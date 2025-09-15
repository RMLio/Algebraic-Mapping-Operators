package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.functions.JoinCondition;

import java.io.ObjectInputStream;
import java.io.Serial;
import java.util.Set;

public abstract class JoinOperator extends BinaryOperator {
    protected final JoinCondition condition;

    /**
     * Instantiates a new join operator.
     *
     * @param operatorName      The name (identifier) of the operator.
     * @param inputFragments    The input fragments of the operator.
     * @param outputFragments   The output fragments of the operator.
     * @param condition         The condition of the join
     */
    protected JoinOperator(String operatorName, Set<String> inputFragments, Set<String> outputFragments, JoinCondition condition) {
        super(operatorName, inputFragments, outputFragments);
        this.condition = condition;
    }

    @Serial
    private void readObject(ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
    }

    public JoinCondition getCondition() {
        return this.condition;
    }
}

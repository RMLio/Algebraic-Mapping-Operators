package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.functions.JoinCondition;

import java.io.ObjectInputStream;
import java.io.Serial;

public abstract class JoinOperator extends BinaryOperator {
    protected final JoinCondition condition;

    protected final String outputFragment;

    public JoinOperator(
            String operatorName, String inputFragment, String outputFragment, JoinCondition condition) {
        super(operatorName, inputFragment);
        this.condition = condition;
        this.outputFragment = outputFragment;
    }

    @Serial
    private void readObject(ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
    }

    public JoinCondition getCondition() {
        return this.condition;
    }
}

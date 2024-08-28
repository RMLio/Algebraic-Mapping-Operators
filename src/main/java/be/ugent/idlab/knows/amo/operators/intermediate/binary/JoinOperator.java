package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import java.io.ObjectInputStream;
import java.io.Serial;

import be.ugent.idlab.knows.amo.functions.JoinCondition;
import be.ugent.idlab.knows.amo.operators.intermediate.unary.RenameOperator;

public abstract class JoinOperator extends BinaryOperator {
    protected final JoinCondition condition;

    protected final RenameOperator renameOperator;
    protected final String outputFragment;

    public JoinOperator(
            String operatorName, String inputFragment, String outputFragment, JoinCondition condition, String alias) {
        super(operatorName, inputFragment);
        this.condition = condition;
        this.outputFragment = outputFragment;
        this.renameOperator = new RenameOperator(operatorName + ":Rename", inputFragment, alias);
    }

    @Serial
    private void readObject(ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
    }

}

package be.ugent.idlab.knows.amo.operators.source;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.operators.Operator;
import be.ugent.idlab.knows.amo.operators.OperatorVisitor;

import java.io.Serializable;

public abstract class SourceOperator extends Operator implements Serializable {

    public SourceOperator(String operatorName) {
        super(operatorName);
    }

    /**
     * Generates a mapping tuple out of the source
     *
     * @return a MappingTuple
     */
    public abstract MappingTuple consumeSource();

    @Override
    public <T> T accept(OperatorVisitor<T> visitor) {
        return visitor.visitSource(this);
    }
}

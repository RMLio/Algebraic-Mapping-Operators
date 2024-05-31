package be.ugent.idlab.knows.amo.operators.source;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.operators.Operator;
import be.ugent.idlab.knows.amo.operators.OperatorVisitor;

import java.io.Serializable;

public interface SourceOperator extends Operator, Serializable {

    /**
     * Generates a mapping tuple out of the source
     *
     * @return a MappingTuple
     */
    MappingTuple consumeSource();

    @Override
    default <T> T visit(OperatorVisitor<T> visitor) {
        return visitor.visitSource(this);
    }
}

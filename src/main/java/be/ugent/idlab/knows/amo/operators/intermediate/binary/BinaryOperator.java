package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.OperatorVisitor;
import be.ugent.idlab.knows.amo.operators.intermediate.IntermediateOperator;

/**
 * Base interface for all binary operators
 * <p>
 * Binary operators accept two SolutionMappings or two MappingTuples and return a single SolutionMapping or a single MappingTuple
 */
public abstract class BinaryOperator extends IntermediateOperator {

    /**
     * @param fragment fragment the operator should operate on
     */
    public BinaryOperator(String operatorName, String fragment) {
        super(operatorName, fragment);
    }

    public abstract SolutionMapping apply(SolutionMapping mapping1, SolutionMapping mapping2);

    public abstract MappingTuple apply(MappingTuple tuple1, MappingTuple tuple2);

    @Override
    public <T> T accept(OperatorVisitor<T> visitor) {
        return visitor.visitBinary(this);
    }
}

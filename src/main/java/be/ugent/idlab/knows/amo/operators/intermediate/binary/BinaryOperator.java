package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.OperatorVisitor;
import be.ugent.idlab.knows.amo.operators.intermediate.IntermediateOperator;

import java.util.Set;

/**
 * Base interface for all binary operators
 * <p>
 * Binary operators accept two SolutionMappings or two MappingTuples and return
 * a single SolutionMapping or a single MappingTuple
 */
public abstract class BinaryOperator extends IntermediateOperator {

    /**
     * @param operatorName The name (identifier) of the operator.
     * @param inputFragments    The input fragments of the operator.
     * @param outputFragments   The output fragments of the operator.
     */
    protected BinaryOperator(String operatorName, Set<String> inputFragments, Set<String> outputFragments) {
        super(operatorName, inputFragments, outputFragments);
    }

    @NonNull
    public abstract BinaryType getBinaryOpType();

    @Nullable
    public abstract SolutionMapping apply(@Nullable SolutionMapping mapping1, @Nullable SolutionMapping mapping2);

    @Nullable
    public abstract MappingTuple apply(@Nullable MappingTuple tuple1, @Nullable MappingTuple tuple2);

    @Override
    @NonNull
    public <T> T accept(@NonNull OperatorVisitor<@NonNull T> visitor) {
        return visitor.visitBinary(this);
    }
}

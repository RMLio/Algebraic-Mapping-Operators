package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.JoinCondition;

import java.util.Set;

public class CrossJoin extends JoinOperator {

    /**
     * Instantiates a new CrossJoin operator (i.e. Cartesian product)
     *
     * @param operatorName      The name (identifier) of the operator.
     * @param inputFragments    The input fragments of the operator.
     * @param outputFragments   The output fragments of the operator.
     * @param condition         The condition of the join
     */
    public CrossJoin(String operatorName, Set<String> inputFragments, Set<String> outputFragments, JoinCondition condition) {
        super(operatorName, inputFragments, outputFragments, condition);
    }

    @Override
    @NonNull
    public BinaryType getBinaryOpType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBinaryOpType'");
    }

    @Override
    public @Nullable SolutionMapping apply(@Nullable SolutionMapping mapping1, @Nullable SolutionMapping mapping2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'apply'");
    }

    @Override
    public @Nullable MappingTuple apply(@Nullable MappingTuple tuple1, @Nullable MappingTuple tuple2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'apply'");
    }

}

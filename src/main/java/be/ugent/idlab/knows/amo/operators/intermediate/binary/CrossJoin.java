package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.JoinCondition;

public class CrossJoin extends JoinOperator {

    public CrossJoin(String operatorName, String inputFragment, String outputFragment, JoinCondition condition,
            String alias) {
        super(operatorName, inputFragment, outputFragment, condition, alias);
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

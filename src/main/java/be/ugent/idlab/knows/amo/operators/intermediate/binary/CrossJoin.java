package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.JoinCondition;

public class CrossJoin extends JoinOperator{

    public CrossJoin(String operatorName, String inputFragment, String outputFragment, JoinCondition condition,
            String alias) {
        super(operatorName, inputFragment, outputFragment, condition, alias);
        //TODO Auto-generated constructor stub
    }

    @Override
    public BinaryType getBinaryOpType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBinaryOpType'");
    }

    @Override
    public SolutionMapping apply(SolutionMapping mapping1, SolutionMapping mapping2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'apply'");
    }

    @Override
    public MappingTuple apply(MappingTuple tuple1, MappingTuple tuple2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'apply'");
    }

}

package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

public class NaturalJoin extends Join {

    /**
     * A convenience constructor where the input fragment is the same as the output fragment
     * @param operatorName
     * @param inputFragment
     */
    public NaturalJoin(String operatorName, String inputFragment) {
        this(operatorName, inputFragment, inputFragment);
    }

    public NaturalJoin(String operatorName, String inputFragment, String outputFragment) {
        this(operatorName, inputFragment, outputFragment, "");
    }

    public NaturalJoin(String operatorName, String inputFragment, String outputFragment, String alias) {
        super(operatorName, inputFragment, outputFragment, SolutionMapping::isCompatibleWith, alias);
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

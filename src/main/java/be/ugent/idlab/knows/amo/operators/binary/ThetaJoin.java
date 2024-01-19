package be.ugent.idlab.knows.amo.operators.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.ThetaFunction;

public record ThetaJoin(ThetaFunction function) implements BinaryOperator {


    @Override
    public SolutionMapping applySolMapping(SolutionMapping mapping1, SolutionMapping mapping2) {
        return null;
    }

    @Override
    public MappingTuple applyMapTuple(MappingTuple tuple1, MappingTuple tuple2) {
        return null;
    }
}

package be.ugent.idlab.knows.amo.operators.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.ThetaFunction;

import java.util.Collection;
import java.util.Set;

public record ThetaJoin(ThetaFunction function) implements BinaryOperator {
    @Override
    public SolutionMapping applySolMapping(SolutionMapping mapping1, SolutionMapping mapping2) {
        if (function.apply(mapping1, mapping2)) {
            return mapping1.union(mapping2);
        }

        return null;
    }

    @Override
    public MappingTuple applyMapTuple(MappingTuple tuple1, MappingTuple tuple2) {
        Set<String> commonFragments = tuple1.commonFragments(tuple2);
        MappingTuple out = new MappingTuple();

        for (String fragment : commonFragments) {
            Collection<SolutionMapping> mappings1 = tuple1.getSolutionMappings(fragment);
            Collection<SolutionMapping> mappings2 = tuple2.getSolutionMappings(fragment);

            for (SolutionMapping mapping1 : mappings1) {
                for (SolutionMapping mapping2 : mappings2) {
                    if (function.apply(mapping1, mapping2)) {
                        SolutionMapping outSolMap = mapping1.union(mapping2);
                        out.addSolutionMap(fragment, outSolMap);
                    }
                }
            }
        }

        return out;
    }
}

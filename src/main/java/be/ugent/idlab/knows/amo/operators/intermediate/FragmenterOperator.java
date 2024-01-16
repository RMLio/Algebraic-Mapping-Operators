package be.ugent.idlab.knows.amo.operators.intermediate;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.FragmentFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record FragmenterOperator(FragmentFunction function) implements IntermediateOperator {


    @Override
    public SolutionMapping applySolMapping(SolutionMapping mapping) {
        throw new IllegalStateException("This operator cannot be called on a Solution Mapping!");
    }

    @Override
    public MappingTuple applyMappingTuple(MappingTuple tuple) {

        List<MappingTuple> newTuples = new ArrayList<>();

        for (String fragment : tuple.getFragments()) {
            Collection<SolutionMapping> solmaps = tuple.getSolutionMappings(fragment);
            MappingTuple newFragments = function.apply(fragment, solmaps);
            newTuples.add(newFragments);
        }

        /* TODO: potential for improvement
            MappingTuple::union method is most performant if the argument is smaller than the callee.
            Consider sorting or a more intelligent algorithm to merge two mapping tuples.
         */
        MappingTuple master = newTuples.get(0);
        int i = 1;
        while (i < newTuples.size()) {
            MappingTuple t = newTuples.get(i);
            master.union(t);
            i++;
        }

        return master;
    }
}

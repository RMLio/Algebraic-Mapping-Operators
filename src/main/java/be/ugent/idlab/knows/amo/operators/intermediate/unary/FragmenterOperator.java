package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.FragmentFunction;
import be.ugent.idlab.knows.amo.operators.OperatorVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * FragmenterOperator splits the fragment into multiple fragments, with potentially different name, as defined by the FragmentFunction
 *
 * @param function function to perform fragmenting with
 */
public class FragmenterOperator implements UnaryOperator {

    private final FragmentFunction function;
    public FragmenterOperator(FragmentFunction function) {
        this.function = function;
    }


    @Override
    public <T> T accept(OperatorVisitor<T> visitor) {
        return null;
    }

    @Override
    public SolutionMapping apply(SolutionMapping mapping) {
        throw new IllegalStateException("This operator cannot be called on a Solution Mapping!");
    }

    @Override
    public MappingTuple apply(MappingTuple tuple) {
        List<MappingTuple> newTuples = new ArrayList<>();

        for (String fragment : tuple.getFragments()) {
            Collection<SolutionMapping> solmaps = tuple.getSolutionMappings(fragment);
            MappingTuple newFragments = this.function.apply(fragment, solmaps);
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
            master = master.union(t);
            i++;
        }

        return master;
    }
}

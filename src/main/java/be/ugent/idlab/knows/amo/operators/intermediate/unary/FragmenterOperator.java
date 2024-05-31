package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.FragmentFunction;

/**
 * FragmenterOperator splits the fragment into multiple fragments, with potentially different name, as defined by the FragmentFunction
 */
public class FragmenterOperator implements UnaryOperator {

    private final FragmentFunction function;

    public FragmenterOperator(FragmentFunction function) {
        this.function = function;
    }

    @Override
    public SolutionMapping apply(SolutionMapping mapping) {
        throw new IllegalStateException("This operator cannot be called on a Solution Mapping!");
    }

    @Override
    public MappingTuple apply(MappingTuple tuple) {
        return this.function.apply(tuple);
    }
}

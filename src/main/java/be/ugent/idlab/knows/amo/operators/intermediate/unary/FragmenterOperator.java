package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import org.jspecify.annotations.Nullable;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.FragmentFunction;

/**
 * FragmenterOperator splits the fragment into multiple fragments, with
 * potentially different name, as defined by the FragmentFunction
 */
public class FragmenterOperator extends UnaryOperator {

    private final FragmentFunction function;

    public FragmenterOperator(String operatorName, String fragment, FragmentFunction function) {
        super(operatorName, fragment);
        this.function = function;
    }

    @Override
    @Nullable
    public SolutionMapping apply(@Nullable SolutionMapping mapping) {
        throw new IllegalStateException("This operator cannot be called on a Solution Mapping!");
    }

    @Override
    public MappingTuple apply(@Nullable MappingTuple tuple) {
        return this.function.apply(tuple);
    }
}

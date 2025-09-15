package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import org.jspecify.annotations.Nullable;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.FragmentFunction;

import java.util.Set;

/**
 * FragmenterOperator splits the fragment into multiple fragments, with
 * potentially different name, as defined by the FragmentFunction
 */
public class FragmenterOperator extends UnaryOperator {

    private final FragmentFunction function;

    /**
     * Instantiates a new "Extend" operator.
     * @param operatorName      The name (identifier) of the operator.
     * @param inputFragments    The input fragments of the operator.
     * @param outputFragments   The output fragments of the operator.
     * @param function          A function handling the actual fragmentation of the output.
     **/
    public FragmenterOperator(String operatorName, Set<String> inputFragments, Set<String> outputFragments, FragmentFunction function) {
        super(operatorName, inputFragments, outputFragments);
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

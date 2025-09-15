package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

public class NaturalJoinOperator extends JoinOperator {


    /**
     * Instantiates a new NaturalJoinOperator
     *
     * @param operatorName      The name (identifier) of the operator.
     * @param inputFragments    The input fragments of the operator.
     * @param outputFragments   The output fragments of the operator.
     */
    public NaturalJoinOperator(String operatorName, Set<String> inputFragments, Set<String> outputFragments) {
        super(operatorName, inputFragments, outputFragments, SolutionMapping::isCompatibleWith);
    }

    @Override
    @NonNull
    public BinaryType getBinaryOpType() {
        return new BinaryType.NaturalJoin();
    }

    @Override
    @Nullable
    public SolutionMapping apply(@Nullable SolutionMapping mapping1, @Nullable SolutionMapping mapping2) {
        if (mapping1 == null || mapping2 == null) {
            return null;
        }

        if (mapping1.isCompatibleWith(mapping2)) {
            return mapping1.union(mapping2);
        }

        return null;

    }

    @Override
    @Nullable
    public MappingTuple apply(@Nullable MappingTuple tuple1, @Nullable MappingTuple tuple2) {
        if (tuple1 == null || tuple2 == null) {
            return null;
        }

        MappingTuple result = new MappingTuple();

        Set<String> commonFragments = tuple1.commonFragments(tuple2);

        for (String fragment : commonFragments) {
            for (SolutionMapping leftMapping : tuple1.getSolutionMappings(fragment)) {
                for (SolutionMapping rightMapping : tuple2.getSolutionMappings(fragment)) {
                    result.addSolutionMap(fragment, this.apply(leftMapping, rightMapping)); // TODO: write to output fragments?
                }
            }
        }

        return result;

    }
}

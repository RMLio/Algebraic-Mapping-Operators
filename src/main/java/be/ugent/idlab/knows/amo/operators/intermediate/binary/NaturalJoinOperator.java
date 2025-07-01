package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

public class NaturalJoinOperator extends JoinOperator {

    /**
     * A convenience constructor where the input fragment is the same as the output
     * fragment
     * 
     * @param operatorName
     * @param inputFragment
     */
    public NaturalJoinOperator(String operatorName, String inputFragment) {
        this(operatorName, inputFragment, inputFragment);
    }

    public NaturalJoinOperator(String operatorName, String inputFragment, String outputFragment) {
        super(operatorName, inputFragment, outputFragment, SolutionMapping::isCompatibleWith);
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
                    result.addSolutionMap(fragment, this.apply(leftMapping, rightMapping));
                }
            }
        }

        return result;

    }
}

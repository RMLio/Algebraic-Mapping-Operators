package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.JoinCondition;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * ThetaJoin implementing a join based on a condition.
 * This class also immediately serves as a base class for all conditional joins.
 */
public class ThetaJoinOperator extends JoinOperator {

    /**
     * Instantiates a new theta join operator.
     *
     * @param operatorName      The name (identifier) of the operator.
     * @param inputFragments    The input fragments of the operator.
     * @param outputFragments   The output fragments of the operator.
     * @param condition         The condition of the join
     */
    public ThetaJoinOperator(String operatorName, Set<String> inputFragments, Set<String> outputFragments, JoinCondition condition) {
        super(operatorName, inputFragments, outputFragments, condition);
    }

    @Override
    @NonNull
    public BinaryType getBinaryOpType() {
        return new BinaryType.ThetaJoin();
    }

    /**
     * Applies a theta join on two solution mappings
     * @param leftMapping  The "left" side of the join.
     * @param rightMapping The "right" side of the join
     * @return  The join result as a solution mapping
     */
    @Override
    @Nullable
    public SolutionMapping apply(@Nullable SolutionMapping leftMapping, @Nullable SolutionMapping rightMapping) {
        if (leftMapping == null || rightMapping == null) {
            return null;
        }

        if (condition.applyCheck(leftMapping, rightMapping)) {
            return leftMapping.union(rightMapping);
        }
        return null;

    }

    @Override
    @Nullable
    public MappingTuple apply(@Nullable MappingTuple tuple1, @Nullable MappingTuple tuple2) {
        if (tuple1 == null || tuple2 == null) {
            return null;
        }

        MappingTuple out = new MappingTuple();

        for (String inputFragment : getInputFragments()) {
            Collection<SolutionMapping> mappings1 = tuple1.getSolutionMappings(inputFragment);
            Collection<SolutionMapping> mappings2 = tuple2.getSolutionMappings(inputFragment);

            for (SolutionMapping mapping1 : mappings1) {
                for (SolutionMapping mapping2 : mappings2) {
                    SolutionMapping solOut = this.apply(mapping1, mapping2);
                    for (String outputFragment : getOutputFragments()) {
                        out.addSolutionMap(outputFragment, solOut);
                    }
                }
            }
        }

        return out;
    }
}

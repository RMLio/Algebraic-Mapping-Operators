package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.JoinCondition;

public class LeftJoinOperator extends JoinOperator {

    /**
     * Instantiates a new join operator.
     *
     * @param operatorName      The name (identifier) of the operator.
     * @param inputFragments    The input fragments of the operator.
     * @param outputFragments   The output fragments of the operator.
     * @param condition         The condition of the join
     */
    public LeftJoinOperator(String operatorName, Set<String> inputFragments, Set<String> outputFragments, JoinCondition condition)  {
        super(operatorName, inputFragments, outputFragments, condition);
    }

    @Override
    @NonNull
    public BinaryType getBinaryOpType() {
        return new BinaryType.LeftJoin();
    }

    @Override
    @Nullable
    public MappingTuple apply(@Nullable MappingTuple tuple1, @Nullable MappingTuple tuple2) {
        if (tuple1 == null) {
            return null;
        }
        if (tuple2 == null) {
            return tuple1;
        }

        for (String inputFragment : getInputFragments()) {
            // left-join on solution mappings of the target fragment
            Collection<SolutionMapping> leftSolMaps = tuple1.getSolutionMappings(inputFragment);
            Collection<SolutionMapping> rightSolMaps = tuple2.getSolutionMappings(inputFragment);
            tuple1.removeFragment(inputFragment);
            List<SolutionMapping> joinedSolMaps = new ArrayList<>();
            for (SolutionMapping leftSolMap : leftSolMaps) {
                for (SolutionMapping rightSolMap : rightSolMaps) {
                    SolutionMapping leftJoined = this.apply(leftSolMap, rightSolMap);
                    joinedSolMaps.add(leftJoined);
                }
            }

            for (String outputFragment : getOutputFragments()) {
                tuple1.setSolutionMaps(outputFragment, joinedSolMaps);
            }
        }

        return tuple1;
    }

    @Override
    @Nullable
    public SolutionMapping apply(@Nullable SolutionMapping mapping1, @Nullable SolutionMapping mapping2) {
        if (mapping1 == null) {
            return null;
        }

        if (mapping2 == null) {
            return mapping1;
        }

        if (this.condition.applyCheck(mapping1, mapping2)) {
            return mapping1.union(mapping2);
        }

        // Don't need to merge solution mappings since the retrieving values for keys in
        // mapping2 will return null anyway
        return mapping1;
    }
}

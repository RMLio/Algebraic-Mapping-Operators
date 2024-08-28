package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.JoinCondition;

public class LeftJoinOperator extends JoinOperator {
    /**
     * A convenience constructor with the output fragment being the same as input
     * fragment
     * 
     * @param operatorname
     * @param inputFragment
     * @param condition
     * @param alias
     */
    public LeftJoinOperator(String operatorname, String inputFragment, JoinCondition condition, String alias) {
        this(operatorname, inputFragment, inputFragment, condition, alias);
    }

    public LeftJoinOperator(String operatorName, String inputFragment, String outputFragment, JoinCondition condition,
            String alias) {
        super(operatorName, inputFragment, outputFragment, condition, alias);
    }

    @Override
    public MappingTuple apply(MappingTuple tuple1, MappingTuple tuple2) {
        MappingTuple result = tuple1;

        // left-join on solution mappings of the target fragment
        Collection<SolutionMapping> leftSolMaps = tuple1.getSolutionMappings(this.fragment);
        Collection<SolutionMapping> rightSolMaps = tuple2.getSolutionMappings(this.fragment);
        result.removeFragment(this.fragment);
        List<SolutionMapping> joinedSolMaps = new ArrayList<>();
        for (SolutionMapping leftSolMap : leftSolMaps) {
            for (SolutionMapping rightSolMap : rightSolMaps) {
                SolutionMapping leftJoined = this.apply(leftSolMap, rightSolMap);
                joinedSolMaps.add(leftJoined);
            }
        }

        result.setSolutionMaps(this.outputFragment, joinedSolMaps);

        return result;
    }

    @Override
    public SolutionMapping apply(SolutionMapping mapping1, SolutionMapping mapping2) {
        if (this.condition.applyCheck(mapping1, mapping2)) {
            return mapping1.union(mapping2);
        }

        // Don't need to merge solution mappings since the retrieving values for keys in
        // mapping2 will return null anyway
        return mapping1;
    }

}

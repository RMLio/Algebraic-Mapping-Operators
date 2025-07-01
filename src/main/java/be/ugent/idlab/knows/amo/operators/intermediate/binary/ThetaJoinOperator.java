package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.JoinCondition;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * ThetaJoin implementing a join based on a condition.
 * This class also immediately serves as a base class for all conditional joins.
 */
public class ThetaJoinOperator extends JoinOperator {
    private static Logger LOG = LoggerFactory.getLogger(ThetaJoinOperator.class);

    public ThetaJoinOperator(String operatorName, String inputFragment, String outputFragment, JoinCondition condition) {
        super(operatorName, inputFragment, outputFragment, condition);
    }

    @Override
    @NonNull
    public BinaryType getBinaryOpType() {
        return new BinaryType.ThetaJoin();
    }

    /**
     * @param leftMapping
     * @param rightMapping
     * @return
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

        Collection<SolutionMapping> mappings1 = tuple1.getSolutionMappings(this.fragment);
        Collection<SolutionMapping> mappings2 = tuple2.getSolutionMappings(this.fragment);

        for (SolutionMapping mapping1 : mappings1) {
            for (SolutionMapping mapping2 : mappings2) {
                SolutionMapping solOut = this.apply(mapping1, mapping2);
                out.addSolutionMap(this.outputFragment, solOut);
            }
        }

        return out;
    }
}

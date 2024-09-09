package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.JoinCondition;
import be.ugent.idlab.knows.amo.operators.intermediate.unary.RenameOperator;

import java.util.Collection;

import org.h2.expression.condition.ConditionInParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ThetaJoin implementing a join based on a condition.
 * This class also immediately serves as a base class for all conditional joins.
 */
public class ThetaJoinOperator extends JoinOperator {
    private static Logger LOG = LoggerFactory.getLogger(ThetaJoinOperator.class);

    public ThetaJoinOperator(String operatorName, String inputFragment, String outputFragment, JoinCondition condition,
            String alias) {
        super(operatorName, inputFragment, outputFragment, condition, alias);
    }

    public static SolutionMapping thetaJoinSolMap(SolutionMapping leftMapping, SolutionMapping rightMapping,
            RenameOperator renameOperator, JoinCondition condition) {
        SolutionMapping m2Aliased = renameOperator.apply(rightMapping);

        LOG.warn(String.format("Join condition: \n %s", condition.toString()));
        LOG.warn(String.format("Renamed solution mapping: \n %s", m2Aliased.toString()));
        if (condition.applyCheck(leftMapping, m2Aliased)) {
            return leftMapping.union(m2Aliased);
        }

        return new SolutionMapping(); // in case the condition doesn't apply, empty solution map is returned

    }

    @Override
    public BinaryType getBinaryOpType() {
        return new BinaryType.ThetaJoin();
    }

    /**
     * This code assumes that mapping2 is already aliased as per the definitions of
     * the paper.
     * No aliasing will be performed here.
     *
     * @param mapping1
     * @param mapping2
     * @return
     */
    @Override
    public SolutionMapping apply(SolutionMapping mapping1, SolutionMapping mapping2) {
        return ThetaJoinOperator.thetaJoinSolMap(mapping1, mapping2, this.renameOperator, this.condition);
    }

    @Override
    public MappingTuple apply(MappingTuple tuple1, MappingTuple tuple2) {
        MappingTuple out = new MappingTuple();

        Collection<SolutionMapping> mappings1 = tuple1.getSolutionMappings(this.fragment);
        Collection<SolutionMapping> mappings2 = tuple2.getSolutionMappings(this.fragment);

        for (SolutionMapping mapping1 : mappings1) {
            for (SolutionMapping mapping2 : mappings2) {
                SolutionMapping solOut = this.apply(mapping1, mapping2);
                if (!solOut.isEmpty()) {
                    out.addSolutionMap(this.outputFragment, solOut);
                }
            }
        }

        return out;
    }
}

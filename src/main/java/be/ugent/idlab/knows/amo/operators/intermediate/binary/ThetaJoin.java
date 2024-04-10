package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.JoinCondition;
import be.ugent.idlab.knows.amo.operators.intermediate.unary.RenameOperator;

import java.util.Collection;
import java.util.Random;
import java.util.Set;

/**
 * ThetaJoin implementing a join based on a condition.
 * This class also immediately serves as a base class for all conditional joins.
 */
public class ThetaJoin implements BinaryOperator {


    protected final JoinCondition condition;

    private final RenameOperator renameOperator;

    public ThetaJoin(JoinCondition condition, String alias) {
        this.condition = condition;
        this.renameOperator = new RenameOperator(alias);
    }

    /**
     * This code assumes that mapping2 is already aliased as per the definitions of the paper.
     * No aliasing will be performed here.
     * @param mapping1
     * @param mapping2
     * @return
     */
    @Override
    public SolutionMapping applySolMapping(SolutionMapping mapping1, SolutionMapping mapping2) {
        SolutionMapping m2Aliased = renameOperator.applySolMapping(mapping2);

        if (this.condition.apply(mapping1, m2Aliased)) {
            return mapping1.union(m2Aliased);
        }

        return new SolutionMapping(); // in case the condition doesn't apply, empty solution map is returned
    }

    @Override
    public MappingTuple applyMapTuple(MappingTuple tuple1, MappingTuple tuple2) {
        Set<String> commonFragments = tuple1.commonFragments(tuple2);
        MappingTuple out = new MappingTuple();

        for (String fragment : commonFragments) {
            Collection<SolutionMapping> mappings1 = tuple1.getSolutionMappings(fragment);
            Collection<SolutionMapping> mappings2 = tuple2.getSolutionMappings(fragment);

            for (SolutionMapping mapping1 : mappings1) {
                for (SolutionMapping mapping2 : mappings2) {
                    SolutionMapping solOut = applySolMapping(mapping1, mapping2);
                    if (!solOut.isEmpty()) {
                        out.addSolutionMap(fragment, solOut);
                    }
                }
            }
        }

        return out;
    }
}

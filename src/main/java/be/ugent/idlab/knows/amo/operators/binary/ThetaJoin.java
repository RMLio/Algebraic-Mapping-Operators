package be.ugent.idlab.knows.amo.operators.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.JoinCondition;

import java.util.Collection;
import java.util.Set;

/**
 * ThetaJoin implementing a join based on a condition.
 * This class also immediately serves as a base class for all conditional joins.
 */
public class ThetaJoin implements BinaryOperator {

    protected final JoinCondition condition;

    public ThetaJoin(JoinCondition condition) {
        this.condition = condition;
    }

    @Override
    public SolutionMapping applySolMapping(SolutionMapping mapping1, SolutionMapping mapping2) {
        if (this.condition.apply(mapping1, mapping2)) {
            return mapping1.union(mapping2);
        }

        return new SolutionMapping();
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
                    out.addSolutionMap(fragment, solOut);
                }
            }
        }

        return out;
    }
}

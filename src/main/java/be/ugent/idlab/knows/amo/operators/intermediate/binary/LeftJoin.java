package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.JoinCondition;

public class LeftJoin extends ThetaJoin {

    public LeftJoin(JoinCondition condition, String alias) {
        super(condition, alias);
    }

    @Override
    public SolutionMapping applySolMapping(SolutionMapping mapping1, SolutionMapping mapping2) {
        if (condition.apply(mapping1, mapping2)) {
            return mapping1.union(mapping2);
        }

        SolutionMapping empty = new SolutionMapping();
        for (String key : mapping2.keySet()) {
            empty.put(key, null);
        }
        return mapping1.union(empty);
    }
}

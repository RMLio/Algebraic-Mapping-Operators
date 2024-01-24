package be.ugent.idlab.knows.amo.operators.binary;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.JoinCondition;

public class RightJoin extends LeftJoin {
    public RightJoin(JoinCondition condition) {
        super(condition);
    }

    @Override
    public SolutionMapping applySolMapping(SolutionMapping mapping1, SolutionMapping mapping2) {
        if (condition.apply(mapping1, mapping2)) {
            return mapping1.union(mapping2);
        }

        SolutionMapping empty = new SolutionMapping();
        for (String key : mapping1.keySet()) {
            empty.put(key, null);
        }
        return empty.union(mapping2);
    }
}

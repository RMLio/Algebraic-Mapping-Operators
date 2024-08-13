package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.JoinCondition;

public class LeftJoin extends Join{
    /**
     * A convenience constructor with the output fragment being the same as input fragment
     * @param operatorname
     * @param inputFragment
     * @param condition
     * @param alias
     */
    public LeftJoin(String operatorname, String inputFragment, JoinCondition condition, String alias) {
        this(operatorname, inputFragment, inputFragment, condition, alias);
    }

    public LeftJoin(String operatorName,String inputFragment, String outputFragment, JoinCondition condition, String alias) {
        super(operatorName, inputFragment, outputFragment, condition, alias);
    }

    @Override
    public SolutionMapping apply(SolutionMapping mapping1, SolutionMapping mapping2) {
        if (this.condition.apply(mapping1, mapping2)) {
            return mapping1.union(mapping2);
        }

        SolutionMapping empty = new SolutionMapping();
        for (String key : mapping2.keySet()) {
            empty.put(key, null);
        }
        return mapping1.union(empty);
    }

    @Override
    public MappingTuple apply(MappingTuple tuple1, MappingTuple tuple2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'apply'");
    }
}

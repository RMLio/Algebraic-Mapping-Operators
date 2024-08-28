package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import java.util.Set;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

public class NaturalJoinOperator extends JoinOperator {

    /**
     * A convenience constructor where the input fragment is the same as the output
     * fragment
     * 
     * @param operatorName
     * @param inputFragment
     */
    public NaturalJoinOperator(String operatorName, String inputFragment) {
        this(operatorName, inputFragment, inputFragment);
    }

    public NaturalJoinOperator(String operatorName, String inputFragment, String outputFragment) {
        this(operatorName, inputFragment, outputFragment, "");
    }

    public NaturalJoinOperator(String operatorName, String inputFragment, String outputFragment, String alias) {
        super(operatorName, inputFragment, outputFragment, SolutionMapping::isCompatibleWith, alias);
    }

    @Override
    public SolutionMapping apply(SolutionMapping mapping1, SolutionMapping mapping2) {
        if (mapping1.isCompatibleWith(mapping2)) {
            return mapping1.union(mapping2);

        }

        return null;

    }

    @Override
    public MappingTuple apply(MappingTuple tuple1, MappingTuple tuple2) {
        // TODO: Auto-generated method stub

        MappingTuple result = new MappingTuple();

        Set<String> commonFragments = tuple1.commonFragments(tuple2);

        for (String fragment : commonFragments) {
            for (SolutionMapping leftMapping : tuple1.getSolutionMappings(fragment)) {
                for (SolutionMapping rightMapping : tuple2.getSolutionMappings(fragment)) {
                    result.addSolutionMap(fragment, this.apply(leftMapping, rightMapping));
                }
            }
        }

        return result;

    }
}

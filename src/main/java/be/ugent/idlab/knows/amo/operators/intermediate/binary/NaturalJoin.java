package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

public class NaturalJoin extends ThetaJoin {

    /**
     * A convenience constructor where the input fragment is the same as the output fragment
     * @param operatorName
     * @param inputFragment
     */
    public NaturalJoin(String operatorName, String inputFragment) {
        this(operatorName, inputFragment, inputFragment);
    }

    public NaturalJoin(String operatorName, String inputFragment, String outputFragment) {
        this(operatorName, inputFragment, outputFragment, "");
    }

    public NaturalJoin(String operatorName, String inputFragment, String outputFragment, String alias) {
        super(operatorName, inputFragment, outputFragment, SolutionMapping::isCompatibleWith, alias);
    }
}

package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

public class NaturalJoin extends ThetaJoin {
    public NaturalJoin() {
        super(SolutionMapping::isCompatibleWith);
    }
}

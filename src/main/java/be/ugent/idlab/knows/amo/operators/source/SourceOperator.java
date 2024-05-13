package be.ugent.idlab.knows.amo.operators.source;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;

import java.io.Serializable;

public interface SourceOperator extends Serializable {

    /**
     * Generates a mapping tuple out of the source
     * @return a MappingTuple
     */
    MappingTuple consumeSource();
}

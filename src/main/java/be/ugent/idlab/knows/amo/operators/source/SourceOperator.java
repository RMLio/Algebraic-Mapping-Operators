package be.ugent.idlab.knows.amo.operators.source;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;

public interface SourceOperator {

    /**
     * Generates a mapping tuple out of the source
     * @return a MappingTuple
     */
    MappingTuple consumeSource();
}

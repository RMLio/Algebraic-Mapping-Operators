package be.ugent.idlab.knows.amo.operators.source;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.operators.Operator;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.SourceIterator;

import java.util.Collection;

/**
 * Operator responsible for reading input into the plan.
 */
public abstract class SourceOperator implements Operator {

    protected final Access access;

    public SourceOperator(Access access) {
        this.access = access;
    }

    /**
     * Generate a collection of MappingTuples from a particular source
     * @return a collection of MappingTuples
     */
    abstract Collection<MappingTuple> getMappingTuples();


    public Collection<MappingTuple> apply(Collection<MappingTuple> tuples) {
        throw new IllegalStateException("Can't call apply on the source operator, use getMappingTuples instead!");
    }
}

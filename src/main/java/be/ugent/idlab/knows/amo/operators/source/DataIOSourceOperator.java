package be.ugent.idlab.knows.amo.operators.source;

import be.ugent.idlab.knows.dataio.access.Access;

/**
 * Operator responsible for reading input into the plan.
 */
public abstract class DataIOSourceOperator implements SourceOperator{

    protected final Access access;

    public DataIOSourceOperator(Access access) {
        this.access = access;
    }

    public Access getAccess() {
        return access;
    }
}

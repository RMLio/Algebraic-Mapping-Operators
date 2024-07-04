package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.operators.source.SourceOperator;
import be.ugent.idlab.knows.dataio.access.Access;

/**
 * Operator responsible for reading input into the plan.
 */
public abstract class DataIOSourceOperator extends SourceOperator {

    protected Access access;
    protected String defaultFragment;

    public DataIOSourceOperator(String operatorName, Access access, String defaultFragment) {

        super(operatorName);
        this.access = access;
        this.defaultFragment = defaultFragment;
    }

    public Access getAccess() {
        return access;
    }
}

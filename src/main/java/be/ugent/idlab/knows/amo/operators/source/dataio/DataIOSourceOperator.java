package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.operators.source.SourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Operator responsible for reading input into the plan.
 */
@NullMarked
public abstract class DataIOSourceOperator extends SourceOperator {

    protected Access access;
    protected String defaultFragment;
    protected List<Field> fields;

    public DataIOSourceOperator(String operatorName, Access access, String defaultFragment, List<Field> fields) {
        super(operatorName);
        this.access = access;
        this.defaultFragment = defaultFragment;
        this.fields = fields;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public String getDefaultFragment() {
        return this.defaultFragment;
    }
}

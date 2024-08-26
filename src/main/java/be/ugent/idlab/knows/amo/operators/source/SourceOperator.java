package be.ugent.idlab.knows.amo.operators.source;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.operators.Operator;
import be.ugent.idlab.knows.amo.operators.OperatorVisitor;

public abstract class SourceOperator extends Operator {
    private transient boolean isReady;

    public SourceOperator(String operatorName) {
        super(operatorName);
        this.isReady = false;
    }

    public MappingTuple next() throws IllegalStateException {
        if (!this.isReady()) {
            throw new IllegalStateException("The source operator hasn't been intialized yet!");
        }
        if (!this.hasNext()) {
            throw new IllegalStateException("No mapping tuples left to be generated");
        }
        return this.nextEffective();
    }

    protected abstract MappingTuple nextEffective();

    public abstract boolean hasNext();

    /**
     * Generates a mapping tuple out of the source
     *
     * @return a MappingTuple
     */
    public abstract MappingTuple consumeSource();

    public abstract String getDefaultFragment();

    public abstract void init() throws Exception;

    public boolean isReady() {
        return this.isReady;
    }

    protected void setReady(boolean value) {
        this.isReady = value;
    }

    @Override
    public <T> T accept(OperatorVisitor<T> visitor) {
        return visitor.visitSource(this);
    }
}

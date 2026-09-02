package be.ugent.idlab.knows.amo.blocks.nodes;

import org.jspecify.annotations.Nullable;

public abstract class AbstractRDFNode implements RDFNode {

    protected Object value;

    public AbstractRDFNode(Object value) {
        // Some JSON libraries will parse numbers as Longs: allow the user to freely specify integers and cast them to long under the hood
        if (value instanceof Integer integer) {
            this.value = integer.longValue();
        } else {
            this.value = value;
        }
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean isLiteral() {
        return false;
    }

    @Override
    public boolean isIRI() {
        return false;
    }

    @Override
    public boolean isBlank() {
        return false;
    }

    @Override
    public boolean isNull() { return false;}

    /**
     * Whether this value stands for several terms rather than one.
     */
    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractRDFNode rdfNode = (AbstractRDFNode) o;

        if (this.isNull() && rdfNode.isNull()) return true;

        return this.value.equals(rdfNode.value);
    }


    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

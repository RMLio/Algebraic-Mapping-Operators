package be.ugent.idlab.knows.amo.blocks.nodes;

import java.io.Serializable;

public abstract class RDFNode implements Serializable {

    protected final Object value;

    public RDFNode(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public abstract boolean isLiteral();

    public abstract boolean isIRI();

    public abstract boolean isBlank();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RDFNode rdfNode = (RDFNode) o;
        return value.equals(rdfNode.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

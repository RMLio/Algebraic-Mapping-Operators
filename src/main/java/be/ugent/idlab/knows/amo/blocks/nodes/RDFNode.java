package be.ugent.idlab.knows.amo.blocks.nodes;

import org.apache.jena.graph.Node;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;

public abstract class RDFNode implements Serializable {

    protected final Object value;

    public RDFNode(Object value) {
        // Some JSON libraries will parse numbers as Longs: allow the user to freely specify integers and cast them to long under the hood
        if (value instanceof Integer integer) {
            this.value = integer.longValue();
        } else {
            this.value = value;
        }
    }

    public Object getValue() {
        return value;
    }

    public boolean isLiteral() {
        return false;
    }

    public boolean isIRI() {
        return false;
    }

    public boolean isBlank() {
        return false;
    }

    public boolean isNull() { return false;}

    public abstract Node getJenaNode();
    
    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RDFNode rdfNode = (RDFNode) o;

        if (this.isNull() && rdfNode.isNull()) return true;

        return this.value.equals(rdfNode.value);
    }


    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

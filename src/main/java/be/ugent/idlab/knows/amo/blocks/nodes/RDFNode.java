package be.ugent.idlab.knows.amo.blocks.nodes;

import org.apache.jena.graph.Node;

import java.io.Serializable;

public abstract class RDFNode implements Serializable {

    protected final Object value;

    public RDFNode(Object value) {
        this.value = value;
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

    public abstract Node getJenaNode();
    
    public abstract String getStringRepr(); 

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

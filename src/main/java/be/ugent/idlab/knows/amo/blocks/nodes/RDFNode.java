package be.ugent.idlab.knows.amo.blocks.nodes;

import org.apache.jena.graph.Node;

import java.io.Serializable;

public interface RDFNode extends Serializable {
    Object getValue();

    void setValue(Object value);

    boolean isLiteral();

    boolean isIRI();

    boolean isBlank();

    boolean isNull();

    boolean isCollection();

    Node getJenaNode();
}

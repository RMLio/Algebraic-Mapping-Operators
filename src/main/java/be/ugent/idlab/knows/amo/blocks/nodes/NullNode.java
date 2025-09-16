package be.ugent.idlab.knows.amo.blocks.nodes;

import org.apache.jena.graph.Node;

public class NullNode extends RDFNode {
    public NullNode() {
        super(null);
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public Node getJenaNode() {
        throw new IllegalStateException("A Jena node cannot be constructed from a null node");
    }

    @Override
    public String toString() {
        return "null";
    }
}

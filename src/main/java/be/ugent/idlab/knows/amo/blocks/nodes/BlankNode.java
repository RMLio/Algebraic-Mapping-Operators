package be.ugent.idlab.knows.amo.blocks.nodes;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BlankNode extends RDFNode {
    public BlankNode(String  value) {
        super(value);
    }

    @Override
    public boolean isBlank() {
        return true;
    }

    @Override
    public Node getJenaNode() {
        return NodeFactory.createBlankNode(this.value.toString());
    }

    @Override
    public String toString() {
        return "_:" + this.value.toString();
    }
}

package be.ugent.idlab.knows.amo.blocks.nodes;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

public class IRINode extends RDFNode {
    public IRINode(String value) {
        super(value);
    }

    @Override
    public boolean isIRI() {
        return true;
    }

    @Override
    public Node getJenaNode() {
        return NodeFactory.createURI(this.value.toString());
    }
}

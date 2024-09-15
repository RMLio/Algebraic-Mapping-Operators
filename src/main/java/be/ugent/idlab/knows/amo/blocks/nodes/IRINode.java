package be.ugent.idlab.knows.amo.blocks.nodes;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.jspecify.annotations.NullMarked;

@NullMarked
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
        // String encoded = URLEncoder.encode(this.value.toString(),
        // StandardCharsets.UTF_8);

        StringBuilder sb = new StringBuilder();
        this.value.toString().chars().forEach(c -> {
            if (c == ' ') {
                sb.append("%20");
            } else if (c == '+') {
                sb.append("%20");
            } else if (c == '*') {
                sb.append("%2A");
            } else {
                sb.append((char) c);
            }
        });

        return NodeFactory.createURI(sb.toString());
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public String getStringRepr() {
        return "<" + this.getJenaNode().toString() + ">";
    }

}

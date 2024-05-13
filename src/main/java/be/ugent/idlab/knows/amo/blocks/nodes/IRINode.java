package be.ugent.idlab.knows.amo.blocks.nodes;

public class IRINode extends RDFNode {
    public IRINode(String value) {
        super(value);
    }

    @Override
    public boolean isLiteral() {
        return false;
    }

    @Override
    public boolean isIRI() {
        return true;
    }

    @Override
    public boolean isBlank() {
        return false;
    }
}

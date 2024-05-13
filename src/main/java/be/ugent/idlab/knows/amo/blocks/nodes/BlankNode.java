package be.ugent.idlab.knows.amo.blocks.nodes;

public class BlankNode extends RDFNode {
    public BlankNode(String value) {
        super(value);
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
        return true;
    }
}

package be.ugent.idlab.knows.amo.blocks.nodes;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum RDFType {
    Literal {
        @Override
        public RDFNode create(String value) {
            return new LiteralNode(value);
        }
    },
    IRI {
        @Override
        public RDFNode create(String value) {
            return new IRINode(value);
        }
    },
    Blank {
        @Override
        public RDFNode create(String value) {
            return new BlankNode(value);
        }
    };

    public abstract RDFNode create(String value);
}

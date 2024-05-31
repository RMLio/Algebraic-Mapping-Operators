package be.ugent.idlab.knows.amo.blocks.nodes;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ext.xerces.impl.dv.XSSimpleType;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

public class LiteralNode extends RDFNode {

    private final String datatype;
    private final String language;

    public LiteralNode(Object value) {
        this(value, "string", "");
    }

    public LiteralNode(Object value, String datatype) {
        this(value, datatype, "");
    }

    public LiteralNode(Object value, String datatype, String language) {
        super(value);
        this.datatype = datatype;
        this.language = language;
    }

    public LiteralNode(Object value, XSDDatatype datatype) {
        this(value, ((XSSimpleType) datatype.extendedTypeDefinition()).getName(), "");
    }

    public LiteralNode(Object value, XSDDatatype datatype, String language) {
        /*
         XSDDatatype is not serializable. Instead of writing a wrapper to enable serializability,
         this library stores the information about the type as a string, with the XSDDatatype reconstructed from it as needed.

         However, XSDDatatype constructor is unable to recognize the URI and instead, this hack needs to be performed:
         Grab the index of the '#' after which the type follows and store that, as datatype.getURI() returns a string pointing to the type in XMLSchema.
         */
        this(value, datatype.getURI().substring(datatype.getURI().indexOf('#') + 1), language);
    }

    public XSDDatatype getDatatype() {
        return new XSDDatatype(this.datatype);
    }

    public String getLanguage() {
        return language;
    }


    public Object getValue() {
        return getDatatype().parse(this.value.toString());
    }

    @Override
    public boolean isLiteral() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LiteralNode that = (LiteralNode) o;
        return this.datatype.equals(that.datatype) && language.equals(that.language);
    }

    @Override
    public Node getJenaNode() {
        return NodeFactory.createLiteral(this.value.toString(), this.language, new XSDDatatype(this.datatype));
    }

    @Override
    public int hashCode() {
        int result = datatype.hashCode();
        result = 31 * result + language.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String out = getValue().toString();
        if (!this.language.isEmpty()) {
            out += "@" + this.language;
        }

        return out;
    }
}



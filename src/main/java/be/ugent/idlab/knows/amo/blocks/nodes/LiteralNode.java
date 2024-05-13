package be.ugent.idlab.knows.amo.blocks.nodes;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ext.xerces.impl.dv.XSSimpleType;

public class LiteralNode extends RDFNode {

    private String datatype;
    private String language;

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
        this(value, datatype.getURI(), language);
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
    public boolean isIRI() {
        return false;
    }

    @Override
    public boolean isBlank() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LiteralNode that = (LiteralNode) o;
        return this.datatype.equals(that.datatype) && language.equals(that.language);
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



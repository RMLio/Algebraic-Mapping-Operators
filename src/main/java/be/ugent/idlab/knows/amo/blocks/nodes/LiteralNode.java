package be.ugent.idlab.knows.amo.blocks.nodes;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ext.xerces.impl.dv.XSSimpleType;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


@NullMarked
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
         * XSDDatatype is not serializable. Instead of writing a wrapper to enable
         * serializability,
         * this library stores the information about the type as a string, with the
         * XSDDatatype reconstructed from it as needed.
         *
         * However, XSDDatatype constructor is unable to recognize the URI and instead,
         * this hack needs to be performed:
         * Grab the index of the '#' after which the type follows and store that, as
         * datatype.getURI() returns a string pointing to the type in XMLSchema.
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
    public boolean equals(@Nullable Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LiteralNode that = (LiteralNode) o;
        return this.datatype.equals(that.datatype) && language.equals(that.language) && this.value.equals(that.value);
    }

    @Override
    public Node getJenaNode() {
        if (this.datatype.equals("string")) {
            return NodeFactory.createLiteral(this.value.toString(), this.language);
        }
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

    @Override
    public String getStringRepr() {
        String out;
        if (this.datatype.equals("double")) {
            out = '"' + formatToScientific(Double.parseDouble(this.value.toString())) + '"';
        } else {
            out = "\"" + value.toString() + "\"";
        }

        // We have to implement this ourselves, because the default jena implementation doesn't put <> around the datatype
        if (!this.language.isEmpty()) {
            out += "@" + this.language;
        } else if (!this.datatype.equals("string")) {
            out += "^^<" + XSDDatatype.XSD + "#" + this.datatype + ">";
        }
        return out;
    }

    private String formatToScientific(Double d) {
        BigDecimal input = BigDecimal.valueOf(d).stripTrailingZeros();
        int precision = input.scale() < 0
                ? input.precision() - input.scale()
                : input.precision();
        StringBuilder s = new StringBuilder("0.0");
        for (int i = 2; i < precision; i++) {
            s.append("#");
        }
        s.append("E0");
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(s.toString());
        return df.format(d);
    }
}

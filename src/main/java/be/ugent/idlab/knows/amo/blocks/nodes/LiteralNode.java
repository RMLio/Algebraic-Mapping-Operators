package be.ugent.idlab.knows.amo.blocks.nodes;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.jena.datatypes.TypeMapper;
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
    private boolean isDatatypeURL = false;

    public LiteralNode(Object value) {
        this(value, "string", "");
    }

    public LiteralNode(Object value, String datatype) {
        this(value, datatype, "");
    }

    /**
     * @param value    value of the node
     * @param datatype datatype of the node. This must either be a valid XSDDatatype name or a fully qualified URL
     * @param language language of the node.
     */
    public LiteralNode(Object value, String datatype, String language) {
        super(value);

        try {
            // we only store String representation of the type, if we know a valid XSDDatatype can be constructed, that's all we need
            new XSDDatatype(datatype);
        } catch (NullPointerException e) {
            if (!UrlValidator.getInstance().isValid(datatype)) {
                throw new IllegalArgumentException("Invalid datatype: must be either XSDDatatype name or a valid URL");
            } else {
                this.isDatatypeURL = true;
            }
        }

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
        if (this.isDatatypeURL) {
            throw new IllegalStateException("Datatype is not an XSDDatatype");
        }
        return new XSDDatatype(this.datatype);
    }

    public String getDatatypeAsString() {
        if (this.isDatatypeURL) {
            return this.datatype;
        }

        return TypeMapper.getInstance().getTypeByName(this.datatype).getURI();
    }

    public String getLanguage() {
        return language;
    }

    public Object getValue() {
        if (this.isDatatypeURL) {
            return "\"%s\"^^<%s>".formatted(this.value.toString(), this.datatype);
        }

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
            if (this.isDatatypeURL) {
                out += "^^<%s>".formatted(this.datatype);
            } else {
                XSDDatatype datatype =  new XSDDatatype(this.datatype);
                out += "^^<%s>".formatted(datatype.getURI());
            }
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

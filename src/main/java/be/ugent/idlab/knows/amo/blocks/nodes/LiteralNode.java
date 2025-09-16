package be.ugent.idlab.knows.amo.blocks.nodes;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.datatypes.xsd.XSDDatatype;
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

    /**
     * Creates a new LiteralNode object with a given value and no language. The data type defaults to String.
     *
     * @param value    value of the node, the lexical form
     */
    public LiteralNode(Object value) {
        this(value, "string", "");
    }

    /**
     * Creates a new LiteralNode object with a given value, data type and no language.
     *
     * @param value    value of the node, the lexical form
     * @param datatype datatype of the node. This must either be a valid XSDDatatype name (e.g.: @{code integer}) or a fully qualified URL
     */
    public LiteralNode(Object value, String datatype) {
        this(value, datatype, "");
    }

    /**
     * Creates a new LiteralNode object with a given value, data type and a given language.
     *
     * @param value    value of the node, the lexical form
     * @param datatype datatype of the node. This must either be a valid XSDDatatype name or a fully qualified URL
     * @param language language of the node. Use an empty String to indicate no language.
     */
    public LiteralNode(Object value, String datatype, String language) {
        super(value);

        if (UrlValidator.getInstance().isValid(datatype)) {
            this.datatype = datatype;
        } else {
            try {
                XSDDatatype xsdDataType = new XSDDatatype(datatype);
                this.datatype = xsdDataType.getURI();
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("Invalid datatype: must be either XSDDatatype name or a valid URL but is \"" + datatype + "\"");
            }
        }
        this.language = language;
    }

    /**
     * Creates a new LiteralNode object with a given value, data type and no language.
     *
     * @param value    value of the node, the lexical form
     * @param datatype datatype of the node.
     */
    public LiteralNode(Object value, XSDDatatype datatype) {
        this(value, datatype.getURI(), "");
    }

    /**
     * Creates a new LiteralNode object with a given value, data type and a given language.
     *
     * @param value    value of the node, the lexical form
     * @param datatype datatype of the node
     * @param language language of the node. Use an empty String to indicate no language.
     */
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
        this(value, datatype.getURI(), language);
    }

    /**
     * Gets the data type of this Literal node.
     * @return  The data type of this node as a fully qualified URL
     */
    public String getDatatype() {
       return this.datatype;
    }

    /**
     * Gets the language tag of this node.
     * @return  The language tag of this node.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Gets the value (lexical form) of this node.
     * @return  The value of this node, rendered as a NQuads term.
     */
    public String getValue() {
        //return "\"%s\"^^<%s>".formatted(this.value.toString(), this.datatype);
        return this.value.toString();
    }

    /**
     * Gets the value object as a Java object.
     * @return  The value object as a Java object. E.g., if the data type is {@code http://www.w3.org/2001/XMLSchema#integer}
     *          and the value is {@code 42}, then a Java Long object with value 42 is returned.
     */
    public Object getValueObject() {
        TypeMapper typeMapper = TypeMapper.getInstance();
        RDFDatatype type = typeMapper.getSafeTypeByName(datatype);
        return type.parse(value.toString());
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
        TypeMapper typeMapper = TypeMapper.getInstance();
        RDFDatatype type = typeMapper.getSafeTypeByName(datatype);
        if (language.isEmpty()) {
            return NodeFactory.createLiteral(getValue(), language, type);
        } else {
            return NodeFactory.createLiteral(getValue(), language, null);
        }
    }

    @Override
    public int hashCode() {
        int result = datatype.hashCode();
        result = 31 * result + language.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String out;
        if (this.datatype.equals("http://www.w3.org/2001/XMLSchema#double")) {
            out = '"' + formatToScientific(Double.parseDouble(this.value.toString())) + '"';
        } else {
            out = "\"" + value.toString() + "\"";
        }

        // We have to implement this ourselves, because the default jena implementation doesn't put <> around the datatype
        if (!this.language.isEmpty()) {
            out += "@" + this.language;
        } else if (!this.datatype.equals("http://www.w3.org/2001/XMLSchema#string")) {
            out += "^^<%s>".formatted(this.datatype);
        }
        return out;
    }

    private String formatToScientific(Double d) {
        BigDecimal input = BigDecimal.valueOf(d).stripTrailingZeros();
        int precision = input.scale() < 0
                ? input.precision() - input.scale()
                : input.precision();
        String s = "0.0" + "#".repeat(Math.max(0, precision - 2)) +
                "E0";
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(s);
        return df.format(d);
    }
}

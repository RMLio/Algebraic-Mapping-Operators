package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.functions.ExtendFunction;
import net.minidev.json.JSONObject;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import java.util.*;

/**
 * A field whose value is produced by a function on the record.
 * <p>
 * The function can be a plain reference into the record, a constant, or any other
 * function. A reference or a constant normally yields a single value, but another
 * function may yield several, in which case the field produces one record per value,
 * much like an {@link IteratorField} does.
 */
public class ExpressionField extends Field {

    private final ExtendFunction expression;

    /**
     * The path the record's references are relative to, {@code null} if they are
     * absolute. Set when a field is read from within an enclosing iterator, as XML
     * sources do.
     */
    private final String referencePrefix;

    /**
     * Reads this field's records, keeping its parsers and compiled paths between them.
     * <p>
     * It is transient because it holds parsers over the record being read; after
     * deserialization it is built again on first use. It also makes this field stateful
     * while it reads, so a field cannot be applied from two threads at once.
     */
    private transient RecordReader reader;

    public ExpressionField(String name, Collection<Field> subfields, ReferenceFormulation referenceFormulation,
                           ExtendFunction expression) {
        this(name, subfields, referenceFormulation, expression, null);
    }

    public ExpressionField(String name, Collection<Field> subfields, ReferenceFormulation referenceFormulation,
                           ExtendFunction expression, String referencePrefix) {
        super(name, subfields, referenceFormulation);
        this.expression = expression;
        this.referencePrefix = referencePrefix;
    }

    public ExtendFunction getExpression() {
        return this.expression;
    }

    /**
     * The attribute this field reads, if its expression is a bare reference into the
     * record.
     *
     * @return the referenced attribute, empty if the expression computes its value
     */
    public Optional<String> getReference() {
        return this.expression == null ? Optional.empty() : this.expression.asReference();
    }

    /**
     * Returns a copy of this field whose references are read relative to the given path.
     *
     * @param prefix the path the references become relative to
     * @return a copy of this field reading relative to {@code prefix}
     */
    public ExpressionField relativeTo(String prefix) {
        return new ExpressionField(this.name, this.subfields, this.referenceFormulation, this.expression, prefix);
    }

    @Override
    public List<SolutionMapping> apply(Optional<String> obj) {
        List<FieldValue> values = evaluate(obj);

        List<SolutionMapping> out = new ArrayList<>();

        // a field that matched nothing still binds its variable, so that the records of
        // the other fields are not lost
        if (values.isEmpty()) {
            out.add(new SolutionMapping(Map.of(
                    this.name, getLiteralNode(null),
                    this.name + ".#", new LiteralNode(0, XSDDatatype.XSDnonNegativeInteger)
            )));

            return out;
        }

        for (int i = 0; i < values.size(); i++) {
            FieldValue value = values.get(i);

            Collection<SolutionMapping> subfieldMaps = applySubfieldsTo(value.subRecord());
            if (subfieldMaps.isEmpty()) {
                out.add(new SolutionMapping(Map.of(
                        this.name, toNode(value.value()),
                        this.name + ".#", new LiteralNode(i, XSDDatatype.XSDnonNegativeInteger)
                )));
            } else {
                for (SolutionMapping sm : subfieldMaps) {
                    sm.put(this.name, toNode(value.value()));
                    sm.put(this.name + ".#", new LiteralNode(i, XSDDatatype.XSDnonNegativeInteger));
                }

                out.addAll(subfieldMaps);
            }
        }

        return out;
    }

    /**
     * One value this field produced, and the record its subfields read to produce theirs.
     * The two differ for XML, where the field binds an element's text while its subfields
     * read the element itself.
     *
     * @param value     the value bound to this field's variable
     * @param subRecord the record handed to the subfields, {@code null} if there is none
     */
    private record FieldValue(Object value, String subRecord) {
    }

    /**
     * Produces the values this field's expression yields for the given record: a bare
     * reference is read straight from the record, since only the reader can follow a path
     * and return the several values it may match; any other function is evaluated against
     * the record's variables.
     *
     * @param obj the raw record
     * @return the values produced, empty if the expression produced no value
     */
    private List<FieldValue> evaluate(Optional<String> obj) {
        if (this.expression == null) {
            return List.of();
        }

        Optional<String> reference = this.expression.asReference();
        if (reference.isPresent()) {
            // an XPath cannot be applied to an element's text, so subfields of an XML
            // field read the element itself
            if (this.referenceFormulation == ReferenceFormulation.XMLPath && !this.subfields.isEmpty()) {
                return reader().readXMLValues(obj, reference.get()).stream()
                        .map(matched -> new FieldValue(matched.text(), matched.element()))
                        .toList();
            }

            return reader().read(obj, reference.get())
                    .stream()
                    .map(value -> new FieldValue(value, serialize(value)))
                    .toList();
        }

        // the variables a computed expression reads are references into the same record,
        // so they are read the same way, relative to the same path
        RecordBinding binding = new RecordBinding(obj, reader());
        return this.expression.apply(binding).stream()
                .map(value -> new FieldValue(value, serialize(value)))
                .toList();
    }

    /**
     * This field's reader, built on first use and kept so that its parsers and compiled
     * paths outlive a single record.
     */
    private RecordReader reader() {
        if (this.reader == null) {
            this.reader = new RecordReader(this.referenceFormulation, this.referencePrefix);
        }

        return this.reader;
    }

    /**
     * Renders a value as the record its subfields read: a JSON object keeps its JSON
     * form, anything else is taken as it reads.
     */
    private static String serialize(Object value) {
        if (value instanceof Map<?, ?> map) {
            return new JSONObject((Map<String, Object>) map).toJSONString();
        }

        return value == null ? null : value.toString();
    }

    /**
     * Applies the subfields to the record a value of this field yielded, naming their
     * variables after this field.
     */
    private Collection<SolutionMapping> applySubfieldsTo(String sub) {
        Collection<SolutionMapping> subfieldMaps =
                this.applySubfields(sub == null ? Optional.empty() : Optional.of(sub));

        for (SolutionMapping sm : subfieldMaps) {
            for (String key : new HashSet<>(sm.keySet())) {
                sm.put(this.name + "." + key, sm.get(key));
                sm.remove(key);
            }
        }

        return subfieldMaps;
    }

    /**
     * A constant carries its own node, so that the term it was declared as survives;
     * anything read from or computed on the record is a literal.
     */
    private RDFNode toNode(Object value) {
        if (this.expression instanceof ConstantExpression constant) {
            return constant.node();
        }

        if (value instanceof RDFNode node) {
            return node;
        }

        return getLiteralNode(value);
    }

    @Override
    public String toString() {
        return "ExpressionField[name=%s,expression=%s,subfields=%s]".formatted(this.name, this.expression, this.subfields);
    }
}

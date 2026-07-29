package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.functions.ExtendFunction;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        List<Object> values = evaluate(obj);

        List<SolutionMapping> out = new ArrayList<>();

        // a field that matched nothing still binds its variable, so that the records of
        // the other fields are not lost
        if (values.isEmpty()) {
            out.add(new SolutionMapping(Map.of(
                    this.name, getLiteralNode(null),
                    this.name + ".#", getLiteralNode(0)
            )));

            return out;
        }

        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);

            Collection<SolutionMapping> subfieldMaps = applySubfieldsTo(value);
            if (subfieldMaps.isEmpty()) {
                out.add(new SolutionMapping(Map.of(
                        this.name, toNode(value),
                        this.name + ".#", getLiteralNode(i)
                )));
            } else {
                for (SolutionMapping sm : subfieldMaps) {
                    sm.put(this.name, toNode(value));
                    sm.put(this.name + ".#", getLiteralNode(i));
                }

                out.addAll(subfieldMaps);
            }
        }

        return out;
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
    private List<Object> evaluate(Optional<String> obj) {
        if (this.expression == null) {
            return List.of();
        }

        Optional<String> reference = this.expression.asReference();
        if (reference.isPresent()) {
            return RecordReader.read(obj, reference.get(), this.referenceFormulation, this.referencePrefix);
        }

        // the variables a computed expression reads are references into the same record,
        // so they are resolved relative to the same path
        RecordBinding binding = new RecordBinding(obj, this.referenceFormulation, this.referencePrefix);
        return new ArrayList<>(this.expression.applyMulti(binding));
    }

    /**
     * Applies the subfields to a value read by this field, naming their variables after
     * this field.
     */
    private Collection<SolutionMapping> applySubfieldsTo(Object value) {
        String sub;
        if (value instanceof Map<?, ?> map) {
            sub = new JSONObject((Map<String, Object>) map).toJSONString();
        } else if (value == null) {
            sub = null;
        } else {
            sub = value.toString();
        }

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

        return getLiteralNode(value);
    }

    @Override
    public String toString() {
        return "ExpressionField[name=%s,expression=%s,subfields=%s]".formatted(this.name, this.expression, this.subfields);
    }
}

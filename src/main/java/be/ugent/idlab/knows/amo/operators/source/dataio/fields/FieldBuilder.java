package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.functions.ExtendFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class FieldBuilder {
    private String name;
    private ReferenceFormulation referenceFormulation;
    private Collection<Field> subfields = new ArrayList<>();
    private Optional<ExtendFunction> expression = Optional.empty();
    private String iterator;

    public FieldBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public FieldBuilder CSV() {
        this.referenceFormulation = ReferenceFormulation.CSVRows;
        return this;
    }

    public FieldBuilder JSON() {
        this.referenceFormulation = ReferenceFormulation.JSONPath;
        return this;
    }

    public FieldBuilder XML() {
        this.referenceFormulation = ReferenceFormulation.XMLPath;
        return this;
    }

    /**
     * Sets the function that produces this field's value from the record (e.g.
     * {@code toUpperCase(name)}, a reference, a constant). The function is applied at read
     * time and the values it produces become the field's values, one record per value.
     */
    public FieldBuilder withExpression(ExtendFunction expression) {
        this.expression = Optional.ofNullable(expression);
        return this;
    }

    /**
     * Sets this field's value to an attribute of the record, read as-is. Shorthand for a
     * {@link ReferenceExpression}.
     */
    public FieldBuilder withReference(String reference) {
        return this.withExpression(reference == null ? null : new ReferenceExpression(reference));
    }

    /**
     * Sets this field's value to the same node for every record. Shorthand for a
     * {@link ConstantExpression}.
     */
    public FieldBuilder withConstant(RDFNode constant) {
        return this.withExpression(constant == null ? null : new ConstantExpression(constant));
    }

    public FieldBuilder withIterator(String iterator) {
        // it is okay for iterator to be null and field be iterable, such as for CSV iterable fields
        this.iterator = iterator;
        return this;
    }

    public FieldBuilder withSubfields(Field... subfields) {
        return this.withSubfields(Arrays.asList(subfields));
    }

    public FieldBuilder withSubfields(Collection<Field> subfields) {
        this.subfields = new ArrayList<>(subfields);
        return this;
    }

    public FieldBuilder withReferenceFormulation(ReferenceFormulation referenceFormulation) {
        return switch (referenceFormulation) {
            case CSVRows -> this.CSV();
            case JSONPath -> this.JSON();
            case XMLPath -> this.XML();
        };
    }

    public Field build() {
        if (this.name == null) {
            throw new IllegalStateException("Field name is required");
        }

        if (this.referenceFormulation == null) {
            throw new IllegalStateException("Reference formulation is required");
        }

        // a field either iterates over the record, making it an IteratorField, or has its
        // value produced by a function on the record, making it an ExpressionField
        if (this.expression.isPresent()) {
            if (this.iterator != null) {
                throw new IllegalStateException("A field must only have either an expression or an iterator defined, not both");
            }

            return new ExpressionField(this.name, this.subfields, this.referenceFormulation, this.expression.get());
        }

        return new IteratorField(this.name, this.subfields, this.referenceFormulation, this.iterator);
    }
}

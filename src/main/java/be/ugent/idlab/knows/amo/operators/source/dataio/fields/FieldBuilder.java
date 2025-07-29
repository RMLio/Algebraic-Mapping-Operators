package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class FieldBuilder {
    private String name;
    private ReferenceFormulation referenceFormulation;
    private Collection<Field> subfields = new ArrayList<>();
    private Optional<String> reference = Optional.empty();
    private Optional<RDFNode> constant = Optional.empty();
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
        this.referenceFormulation = ReferenceFormulation.XPath;
        return this;
    }

    public FieldBuilder withReference(String reference) {
        if (reference == null) {
            this.reference = Optional.empty();
        } else {
            this.reference = Optional.of(reference);
        }
        return this;
    }

    public FieldBuilder withConstant(RDFNode constant) {
        if (constant == null) {
            this.constant = Optional.empty();
        } else {
            this.constant = Optional.of(constant);
        }
        return this;
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
            case XPath -> this.XML();
        };
    }

    public Field build() {
        if (this.name == null) {
            throw new IllegalStateException("Field name is required");
        }

        if (this.referenceFormulation == null) {
            throw new IllegalStateException("Reference formulation is required");
        }

        // each field has either a reference or a constant (making it ExpressionField) or an iterator (making it IterableField)
        if (this.reference.isPresent()) {
            // constant and iterator must not be present
            if (this.constant.isPresent() || this.iterator != null) {
                throw new IllegalStateException("A field must only have either reference, constant or iterator defined, not a combination of these");
            }

            return new ReferenceField(name, this.subfields, this.referenceFormulation, this.reference.get());
        }

        if (this.constant.isPresent()) {
            if (this.iterator != null) {
                throw new IllegalStateException("A field must only have either reference, constant or iterator defined, not a combination of these");
            }
            return new ConstantField(this.name, this.subfields, this.referenceFormulation, this.constant.get());
        }

        return new IteratorField(this.name, this.subfields, this.referenceFormulation, this.iterator);
    }
}

package be.ugent.idlab.knows.amo.operators.source.dataio.builders;

import be.ugent.idlab.knows.amo.operators.source.Compression;
import be.ugent.idlab.knows.amo.operators.source.SourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.XMLSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class XMLSourceOperatorBuilder extends SourceOperatorBuilder {
    private String rootIterator = null;

    public XMLSourceOperatorBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public XMLSourceOperatorBuilder withAccess(Access access) {
        this.access = Optional.of(access);
        return this;
    }

    public XMLSourceOperatorBuilder withFragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    public XMLSourceOperatorBuilder withRootIterator(String rootIterator) {
        this.rootIterator = rootIterator;
        return this;
    }

    @Override
    public XMLSourceOperatorBuilder withField(Field field) {
        this.fields.add(field);
        return this;
    }

    @Override
    public XMLSourceOperatorBuilder withFields(Field... fields) {
        this.fields.addAll(Arrays.asList(fields));
        return this;
    }

    public XMLSourceOperatorBuilder withFields(Collection<Field> fields) {
        this.fields.addAll(fields);
        return this;
    }

    @Override
    public SourceOperator build() {
        if (this.rootIterator == null) {
            throw new IllegalArgumentException("Root iterator must be set");
        }
        if (this.access.isEmpty()) {
            throw new IllegalArgumentException("Access field must be set");
        }

        return new XMLSourceOperator(this.name, this.access.get(), this.fragment, this.rootIterator, this.fields);
    }

    @Override
    public XMLSourceOperatorBuilder withCompression(Compression compression) {
        this.compression = compression;
        return this;
    }
}

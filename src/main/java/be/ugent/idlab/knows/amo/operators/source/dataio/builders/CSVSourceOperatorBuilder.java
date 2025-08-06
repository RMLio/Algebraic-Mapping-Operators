package be.ugent.idlab.knows.amo.operators.source.dataio.builders;

import be.ugent.idlab.knows.amo.operators.source.SourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.CSVSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class CSVSourceOperatorBuilder extends SourceOperatorBuilder {
    public CSVSourceOperatorBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CSVSourceOperatorBuilder withAccess(Access access) {
        this.access = Optional.of(access);
        return this;
    }

    public CSVSourceOperatorBuilder withFragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    public CSVSourceOperatorBuilder withField(Field field) {
        this.fields.add(field);
        return this;
    }

    public CSVSourceOperatorBuilder withFields(Field... fields) {
        this.fields.addAll(Arrays.asList(fields));
        return this;
    }

    public CSVSourceOperatorBuilder withFields(Collection<Field> fields) {
        this.fields.addAll(fields);
        return this;
    }

    @Override
    public SourceOperator build() {
        if (this.access.isEmpty()) {
            throw new IllegalArgumentException("Access field must be set");
        }

        return new CSVSourceOperator(this.name, this.access.get(), this.fragment, this.fields);
    }
}

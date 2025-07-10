package be.ugent.idlab.knows.amo.operators.source.dataio.builders;

import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.operators.source.SourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.JSONSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.ExpressionField;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class JSONSourceOperatorBuilder extends SourceOperatorBuilder {

    private String rootIterator = null;
    private List<Field> fields = new ArrayList<>();

    public JSONSourceOperatorBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public JSONSourceOperatorBuilder withAccess(Access access) {
        this.access = Optional.of(access);
        return this;
    }

    public JSONSourceOperatorBuilder withFragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    public JSONSourceOperatorBuilder withRootIterator(String rootIterator) {
        this.rootIterator = rootIterator;
        return this;
    }

    public JSONSourceOperatorBuilder withField(@NonNull Field field) {
        this.fields.add(field);
        return this;
    }

    public JSONSourceOperatorBuilder withField(String name, String iterator) {
        this.fields.add(new ExpressionField(name, iterator));
        return this;
    }

    public JSONSourceOperatorBuilder withField(String iterator) {
        this.fields.add(new ExpressionField(iterator, iterator));
        return this;
    }

    public JSONSourceOperatorBuilder withDefaultField(String name, RDFNode value) {
        this.fields.add(new ExpressionField(name, value));
        return this;
    }

    public JSONSourceOperatorBuilder withFields(Collection<Field> fields) {
        this.fields.addAll(fields);
        return this;
    }

    public JSONSourceOperatorBuilder withFields(Field... fields) {
        return this.withFields(Arrays.asList(fields));
    }

    @Override
    public SourceOperator build() {
        if (this.access.isEmpty()) {
            throw new IllegalArgumentException("Access field must be set");
        }
//        if (this.rootVariables.isEmpty()) {
//            throw new IllegalArgumentException("This operator cannot dynamically recognize present variables, set them using .withRootVariables()");
//        }
        if (this.rootIterator == null) {
            throw new IllegalArgumentException("Root iterator must be set");
        }

        return new JSONSourceOperator(this.name, this.access.get(), this.fragment, this.rootIterator, this.fields);
    }
}

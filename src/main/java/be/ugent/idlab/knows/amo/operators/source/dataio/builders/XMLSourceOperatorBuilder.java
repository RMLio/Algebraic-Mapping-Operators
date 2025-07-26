package be.ugent.idlab.knows.amo.operators.source.dataio.builders;

import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.operators.source.SourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.XMLSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class XMLSourceOperatorBuilder extends SourceOperatorBuilder {

    private Collection<String> rootVariables = new ArrayList<>();
    private String rootIterator = null;
    private List<String> subIterators = new ArrayList<>();

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

    public XMLSourceOperatorBuilder withRootVariables(@NonNull Collection<String> rootVariables) {
        this.rootVariables = rootVariables;
        return this;
    }

    public XMLSourceOperatorBuilder withRootVariable(String rootVariable) {
        this.rootVariables.add(rootVariable);
        return this;
    }

    public XMLSourceOperatorBuilder withRootIterator(String rootIterator) {
        this.rootIterator = rootIterator;
        return this;
    }

    public XMLSourceOperatorBuilder withSubIterators(Collection<String> subIterators) {
        this.subIterators = new ArrayList<>(subIterators);
        return this;
    }

    public XMLSourceOperatorBuilder withSubIterator(String subIterator) {
        this.subIterators.add(subIterator);
        return this;
    }

    @Override
    public SourceOperator build() {
//        if (this.rootVariables.isEmpty()) {
//            throw new IllegalArgumentException("This operator cannot dynamically recognize present variables, set them using .withRootVariables()");
//        }
        if (this.rootIterator == null) {
            throw new IllegalArgumentException("Root iterator must be set");
        }
        if (this.access.isEmpty()) {
            throw new IllegalArgumentException("Access field must be set");
        }


        return new XMLSourceOperator(this.name, this.access.get(), this.fragment, this.rootIterator, this.fields);
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
}

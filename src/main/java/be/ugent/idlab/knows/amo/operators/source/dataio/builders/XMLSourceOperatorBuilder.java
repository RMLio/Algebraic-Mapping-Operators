package be.ugent.idlab.knows.amo.operators.source.dataio.builders;

import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.operators.source.SourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.XMLSourceOperator;
import be.ugent.idlab.knows.dataio.access.Access;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class XMLSourceOperatorBuilder extends SourceOperatorBuilder {

    private Collection<String> rootVariables = new ArrayList<>();
    private String rootIterator = null;
    private List<String> subIterators = new ArrayList<>();

    public XMLSourceOperatorBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public XMLSourceOperatorBuilder withAccess(Access access) {
        this.access = access;
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

    public XMLSourceOperatorBuilder withAliases(Collection<Pair<String, String>> aliases) {
        this.aliases = new ArrayList<>(aliases);
        return this;
    }

    public XMLSourceOperatorBuilder withAlias(String from, String to) {
        this.aliases.add(new Pair<>(from, to));
        return this;
    }

    public XMLSourceOperatorBuilder withDefaultValues(Map<String, RDFNode> defaultValues) {
        this.defaultValues = defaultValues;
        return this;
    }

    public XMLSourceOperatorBuilder withDefaultValue(String key, RDFNode defaultValue) {
        this.defaultValues.put(key, defaultValue);
        return this;
    }

    public XMLSourceOperatorBuilder withDefaultStringValue(String key, String value) {
        this.defaultValues.put(key, new LiteralNode(value, XSDDatatype.XSDstring));
        return this;
    }


    @Override
    public SourceOperator build() {
        if (this.rootVariables.isEmpty()) {
            throw new IllegalArgumentException("This operator cannot dynamically recognize present variables, set them using .withRootVariables()");
        }
        if (this.rootIterator == null) {
            throw new IllegalArgumentException("Root iterator must be set");
        }


        return new XMLSourceOperator(this.name, this.access, this.fragment, this.rootVariables, this.rootIterator, this.subIterators, this.aliases, this.defaultValues);
    }
}

package be.ugent.idlab.knows.amo.operators.source.dataio.builders;

import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.operators.source.SourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.JSONSourceOperator;
import be.ugent.idlab.knows.dataio.access.Access;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class JSONSourceOperatorBuilder extends SourceOperatorBuilder {

    private Collection<String> rootVariables = new ArrayList<>();
    private String rootIterator = null;
    private List<String> subIterators = new ArrayList<>();

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

    public JSONSourceOperatorBuilder withRootVariables(@NonNull Collection<String> rootVariables) {
        this.rootVariables = rootVariables;
        return this;
    }

    public JSONSourceOperatorBuilder withRootVariable(String rootVariable) {
        this.rootVariables.add(rootVariable);
        return this;
    }

    public JSONSourceOperatorBuilder withRootIterator(String rootIterator) {
        this.rootIterator = rootIterator;
        return this;
    }

    public JSONSourceOperatorBuilder withSubIterators(Collection<String> subIterators) {
        this.subIterators = new ArrayList<>(subIterators);
        return this;
    }

    public JSONSourceOperatorBuilder withSubIterator(String subIterator) {
        this.subIterators.add(subIterator);
        return this;
    }

    public JSONSourceOperatorBuilder withAliases(Collection<Pair<String, String>> aliases) {
        this.aliases = new ArrayList<>(aliases);
        return this;
    }

    public JSONSourceOperatorBuilder withAlias(String from, String to) {
        this.aliases.add(new Pair<>(from, to));
        return this;
    }

    public JSONSourceOperatorBuilder withDefaultValue(String key, RDFNode value) {
        this.defaultValues.put(key, value);
        return this;
    }

    public JSONSourceOperatorBuilder withDefaultStringValue(String key, String value) {
        this.defaultValues.put(key, new LiteralNode(value, XSDDatatype.XSDstring));
        return this;
    }

    public JSONSourceOperatorBuilder withDefaultValues(Map<String, RDFNode> defaultValues) {
        this.defaultValues = defaultValues;
        return this;
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


        return new JSONSourceOperator(this.name, this.access.get(), this.fragment, this.rootVariables, this.rootIterator, this.subIterators, this.aliases, this.defaultValues);
    }
}

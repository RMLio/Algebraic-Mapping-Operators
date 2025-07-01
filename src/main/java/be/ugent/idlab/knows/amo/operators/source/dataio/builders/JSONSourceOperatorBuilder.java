package be.ugent.idlab.knows.amo.operators.source.dataio.builders;

import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.operators.source.SourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.JSONSourceOperator;
import be.ugent.idlab.knows.dataio.access.Access;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JSONSourceOperatorBuilder extends SourceOperatorBuilder {

    private Collection<String> rootVariables = new ArrayList<>();
    private String rootIterator = null;
    private List<String> subIterators = new ArrayList<>();
    private List<Pair<String, String>> aliases = new ArrayList<>();

    public JSONSourceOperatorBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public JSONSourceOperatorBuilder withAccess(Access access) {
        this.access = access;
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

    @Override
    public SourceOperator build() {
        if (this.rootVariables.isEmpty()) {
            throw new IllegalArgumentException("This operator cannot dynamically recognize present variables, set them using .withRootVariables()");
        }
        if (this.rootIterator == null) {
            throw new IllegalArgumentException("Root iterator must be set");
        }


        return new JSONSourceOperator(this.name, this.access, this.fragment, this.rootVariables, this.rootIterator, this.subIterators, this.aliases);
    }
}

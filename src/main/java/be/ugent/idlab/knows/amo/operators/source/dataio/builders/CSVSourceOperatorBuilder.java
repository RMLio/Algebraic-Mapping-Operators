package be.ugent.idlab.knows.amo.operators.source.dataio.builders;

import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.operators.source.SourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.CSVSourceOperator;
import be.ugent.idlab.knows.dataio.access.Access;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import java.util.List;
import java.util.Map;
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

    public CSVSourceOperatorBuilder withAliases(List<Pair<String, String>> aliases) {
        this.aliases = aliases;
        return this;
    }

    public CSVSourceOperatorBuilder withAlias(String first, String second) {
        this.aliases.add(new Pair<>(first, second));
        return this;
    }

    public CSVSourceOperatorBuilder withDefaultValues(Map<String, RDFNode> defaultValues) {
        this.defaultValues = defaultValues;
        return this;
    }

    public CSVSourceOperatorBuilder withDefaultValue(String key, RDFNode value) {
        this.defaultValues.put(key, value);
        return this;
    }

    public CSVSourceOperatorBuilder withDefaultStringValue(String key, String value) {
        this.defaultValues.put(key, new LiteralNode(value, XSDDatatype.XSDstring));
        return this;
    }

    @Override
    public SourceOperator build() {
        if (this.access.isEmpty()) {
            throw new IllegalArgumentException("Access field must be set");
        }

        return new CSVSourceOperator(this.name, this.access.get(), this.fragment, this.aliases, this.defaultValues);
    }

}

package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.functions.ExtendFunction;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * The expression of a field that reads an attribute of the record as-is: a CSV column, a
 * JSONPath, an XPath. The field reads the attribute straight from the record rather than
 * evaluating this function, which is what {@link #asReference()} tells it to do.
 *
 * @param reference the attribute read
 */
public record ReferenceExpression(String reference) implements ExtendFunction {

    @Override
    @Nullable
    public String apply(@Nullable SolutionMapping mapping) {
        if (mapping == null) {
            return null;
        }

        RDFNode value = mapping.get(this.reference);
        return value == null || value.isNull() ? null : value.getValue().toString();
    }

    @Override
    public Optional<String> asReference() {
        return Optional.of(this.reference);
    }

    @Override
    public String toString() {
        return "Reference[%s]".formatted(this.reference);
    }
}

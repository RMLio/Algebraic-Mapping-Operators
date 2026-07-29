package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.functions.ExtendFunction;
import org.jspecify.annotations.Nullable;

/**
 * The expression of a field that has the same value for every record, regardless of what
 * the record holds. The node is kept as it was declared, so that a constant that is an
 * IRI stays one.
 *
 * @param node the value of every record's field
 */
public record ConstantExpression(RDFNode node) implements ExtendFunction {

    @Override
    @Nullable
    public String apply(@Nullable SolutionMapping mapping) {
        return this.node == null || this.node.isNull() ? null : this.node.getValue().toString();
    }

    @Override
    public String toString() {
        return "Constant[%s]".formatted(this.node);
    }
}

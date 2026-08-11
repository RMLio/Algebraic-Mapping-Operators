package be.ugent.idlab.knows.amo.functions;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFType;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;

/**
 * A function that defines how the new value should be generated.
 */
@FunctionalInterface
public interface ExtendFunction extends Serializable {

    @Nullable
    default RDFNode applyToNode(@Nullable SolutionMapping mapping) {
        String innerValue = this.apply(mapping);
        if (innerValue == null) {
            return null;
        }
        if (this.getRDFTypeOpt().isPresent()) {
            return this.getRDFTypeOpt().get().create(innerValue);
        } else {
            return RDFType.Literal.create(innerValue);
        }
    }

    default Optional<RDFType> getRDFTypeOpt() {
        return Optional.empty();
    }

    /**
     * Applies the function and returns all values it produces.
     * <p>
     * A Reference or a Constant yields a single value, which is why the default
     * implementation simply wraps {@link #apply(SolutionMapping)}. Other functions may
     * produce several values; those override this method, and the field they belong to
     * then produces one record per value (much like an iterator field does).
     *
     * @param mapping the solution mapping to evaluate against
     * @return the values produced, empty if the function produced no value
     */
    default List<String> applyMulti(@Nullable SolutionMapping mapping) {
        String value = this.apply(mapping);
        return value == null ? List.of() : List.of(value);
    }

    /**
     * Applies the function and returns a node for every value it produces.
     * <p>
     * The counterpart of {@link #applyMulti(SolutionMapping)} for the places that build
     * terms rather than read values: a function producing several values makes the
     * operator using it produce a record per value. A function producing a single value,
     * which is most of them, needs nothing beyond {@link #applyToNode(SolutionMapping)},
     * which is what the default returns.
     *
     * @param mapping the solution mapping to evaluate against
     * @return a node per value produced, empty if the function produced none
     */
    default List<RDFNode> applyMultiToNode(@Nullable SolutionMapping mapping) {
        RDFNode node = this.applyToNode(mapping);
        return node == null ? List.of() : List.of(node);
    }

    /**
     * The attribute this function reads, if the function is a bare reference into the
     * record (e.g. a CSV column, a JSONPath or an XPath).
     * <p>
     * A bare reference is evaluated by reading the attribute straight from the record
     * rather than by evaluating the function against a solution mapping: only the reader
     * knows how to follow a path into the raw data, and only it can return the several
     * values a path may match (a JSON array, an XML node list). Functions that compute a
     * value from the record's variables leave this empty and are evaluated through
     * {@link #applyMulti(SolutionMapping)} instead.
     *
     * @return the referenced attribute, empty if this function is not a bare reference
     */
    default Optional<String> asReference() {
        return Optional.empty();
    }

    @Nullable
    String apply(@Nullable SolutionMapping mapping);

}

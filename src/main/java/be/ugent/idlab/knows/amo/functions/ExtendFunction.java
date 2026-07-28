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

    @Nullable
    String apply(@Nullable SolutionMapping mapping);

}

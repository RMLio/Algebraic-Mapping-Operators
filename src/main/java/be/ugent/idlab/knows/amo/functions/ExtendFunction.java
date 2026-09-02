package be.ugent.idlab.knows.amo.functions;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFType;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * A function that defines how the new value should be generated.
 */
@FunctionalInterface
public interface ExtendFunction extends Serializable {

    default Optional<RDFType> getRDFTypeOpt() {
        return Optional.empty();
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
     * {@link #apply(SolutionMapping)} instead.
     *
     * @return the referenced attribute, empty if this function is not a bare reference
     */
    default Optional<String> asReference() {
        return Optional.empty();
    }

    /**
     * Applies this function to a solution mapping, producing one or more RDF nodes.
     * @param mapping the solution mapping to apply the function to.
     * @return a list of RDF nodes produced by the function
     */
    @Nullable
    List<RDFNode> apply(@Nullable SolutionMapping mapping);

}

package be.ugent.idlab.knows.amo.functions;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFType;

import java.io.Serializable;
import java.util.Optional;

/**
 * A function that defines how the new value should be generated.
 */
@FunctionalInterface
public interface ExtendFunction extends Serializable {

    default RDFNode applyToNode(SolutionMapping mapping) {
        String innerValue = this.apply(mapping);
        if (this.getRDFTypeOpt().isPresent()) {
            return this.getRDFTypeOpt().get().create(innerValue);
        } else {
            return RDFType.Literal.create(innerValue);
        }
    }

    default Optional<RDFType> getRDFTypeOpt() {
        return Optional.empty();
    }

    String apply(SolutionMapping mapping);

}

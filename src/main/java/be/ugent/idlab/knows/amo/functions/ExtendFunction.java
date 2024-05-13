package be.ugent.idlab.knows.amo.functions;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import org.apache.jena.graph.Node;

import java.io.Serializable;

/**
 * A function that defines how the new value should be generated.
 */
@FunctionalInterface
public interface ExtendFunction extends Serializable {
    RDFNode apply(SolutionMapping mapping);

}

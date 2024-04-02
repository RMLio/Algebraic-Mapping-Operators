package be.ugent.idlab.knows.amo.functions;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import org.apache.jena.graph.Node;

/**
 * A function that defines how the new value should be generated.
 */
@FunctionalInterface
public interface ExtendFunction {
    Node apply(SolutionMapping mapping);

}

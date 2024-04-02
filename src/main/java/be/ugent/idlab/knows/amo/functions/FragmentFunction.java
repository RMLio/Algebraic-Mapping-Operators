package be.ugent.idlab.knows.amo.functions;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

import java.util.Collection;

/**
 * A function that determines how a particular fragment should be fragmented into new fragments.
 * <p>
 * If the solution mapping contains variables that the function can be applied on, the resulting MappingTuple must
 * return the solution mappings resulting from applying the function.
 * Otherwise, the function must return a MappingTuple containing the original SolutionMappings.
 */
public interface FragmentFunction {
    MappingTuple apply(String fragment, Collection<SolutionMapping> mapping);
}

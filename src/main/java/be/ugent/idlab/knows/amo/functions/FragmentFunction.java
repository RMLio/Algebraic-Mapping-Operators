package be.ugent.idlab.knows.amo.functions;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

import java.io.Serializable;
import java.util.Collection;

/**
 * A function that determines how a particular fragment should be fragmented into new fragments.
 * <p>
 * If the solution mapping contains variables that the function can be applied on, the resulting MappingTuple must
 * return the solution mappings resulting from applying the function.
 * Otherwise, the function must return a MappingTuple containing the original SolutionMappings.
 * <br>
 * Theory suggests that this function must work on individual Solution Mappings.
 * In implementation, we have the function transform a MappingTuple directly.
 */
public interface FragmentFunction extends Serializable {
    /**
     * Applies the fragment function
     * @return a MappingTuple with the function applied.
     */
    MappingTuple apply(MappingTuple tuple);
}

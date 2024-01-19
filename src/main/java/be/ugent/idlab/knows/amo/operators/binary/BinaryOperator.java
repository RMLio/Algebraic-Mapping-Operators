package be.ugent.idlab.knows.amo.operators.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

/**
 * Base interface for all binary operators
 * <p>
 * Binary operators accept two SolutionMappings or two MappingTuples and return a single SolutionMapping or a single MappingTuple
 */
public interface BinaryOperator {

    SolutionMapping applySolMapping(SolutionMapping mapping1, SolutionMapping mapping2);

    MappingTuple applyMapTuple(MappingTuple tuple1, MappingTuple tuple2);
}

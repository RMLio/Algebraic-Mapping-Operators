package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.Operator;
import org.apache.jena.atlas.lib.NotImplemented;

import java.util.Collection;
import java.util.List;

/**
 * Base interface for all binary operators
 * <p>
 * Binary operators accept two SolutionMappings or two MappingTuples and return a single SolutionMapping or a single MappingTuple
 */
public interface BinaryOperator extends Operator {

    SolutionMapping applySolMapping(SolutionMapping mapping1, SolutionMapping mapping2);

    MappingTuple applyMapTuple(MappingTuple tuple1, MappingTuple tuple2);

    @Override
    default Collection<MappingTuple> apply(Collection<MappingTuple> tuples) {
        throw new NotImplemented("Joins not yet supported!");
    }
}

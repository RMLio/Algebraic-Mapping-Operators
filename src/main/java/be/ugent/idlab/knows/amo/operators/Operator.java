package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;

import java.util.Collection;

/**
 * Marker interface for operators
 */
public interface Operator {
    Collection<MappingTuple> apply(Collection<MappingTuple> tuples);
}

package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.dataio.iterators.SourceIterator;
import be.ugent.idlab.knows.dataio.record.Record;
import org.apache.jena.graph.NodeFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementation of the SourceOperator using DataIO.
 * As DataIO is written with large files in mind, it isn't always possible to see all possible variable names.
 * For example: when processing JSON files, DataIO has no way of inferring all possible values paths of the document that end up in a value.
 * Therefore, this operator must be provided with a collection of references that must appear in the MappingTuples
 */
public record DataIOSourceOperator(SourceIterator iterator, Collection<String> references) implements SourceOperator {
    @Override
    public Collection<MappingTuple> getMappingTuples() {
        MappingTuple tuple = new MappingTuple();

        while (this.iterator.hasNext()) {
            SolutionMapping mapping = new SolutionMapping();
            Record s = this.iterator.next();
            for (String reference : references) {
                String value = s.get(reference).get(0).toString();
                mapping.put(reference, NodeFactory.createLiteral(value));
            }

            tuple.addSolutionMap("f_default", mapping);
        }

        return Collections.singleton(tuple);
    }
}

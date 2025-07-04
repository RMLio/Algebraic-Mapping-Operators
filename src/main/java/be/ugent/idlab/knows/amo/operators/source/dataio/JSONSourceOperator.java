package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.NullNode;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.record.Record;
import be.ugent.idlab.knows.dataio.record.RecordValue;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.jspecify.annotations.NonNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Implementation of the SourceOperator using DataIO for JSON sources.
 * DataIO is written with infinite sources in mind: it provides no way to
 * inspect all variables present in the stream.
 * For this reason, this source must be provided with variables to be read and
 * included in the MappingTuple
 */
public class JSONSourceOperator extends DataIOSourceOperator {

    private final String rootIterator;
    private final List<Field> fields;
    private final Deque<SolutionMapping> solutionMappingQueue = new ArrayDeque<>();
    private transient JSONSourceIterator sourceIterator;

    public JSONSourceOperator(String operatorName, Access access, String defaultFragment,
                              String rootIterator,
                              List<Field> fields) {
        super(operatorName, access, defaultFragment);
        this.rootIterator = rootIterator;
        this.fields = fields;
        this.sourceIterator = null;
    }

    @Override
    @NonNull
    public MappingTuple consumeSource() {
        MappingTuple tuple = new MappingTuple();
        try {
            this.init();
            JSONSourceIterator iterator = this.sourceIterator;
            while (iterator.hasNext()) {
                queueNextSolutionMappings();
                // put queue contents in tuple, and clear
                while (!solutionMappingQueue.isEmpty()) {
                    tuple.addSolutionMap(this.defaultFragment, solutionMappingQueue.poll());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return tuple;
    }

    // Multiple solutions are queued if there is lists usage (e.g. authors[*]) -> {book1, author:author1}, {book1, author, author2}
    private void queueNextSolutionMappings() {
        Record r = sourceIterator.next();

        List<SolutionMapping> maps = new ArrayList<>();
        maps.add(new SolutionMapping());

        for (Field f : fields) {

            if (f.constant() != null) {
                maps.forEach(m -> m.put(f.name(), f.constant()));
                continue;
            }

            RecordValue recordValue = r.get(f.iterator());

            if (recordValue.isOk()) {
                Object value = recordValue.getValue();
                if (value instanceof ArrayList<?> jsonArray) {

                    if (jsonArray.isEmpty()) {
                        continue; // don't add empty list variables
                    }

                    List<SolutionMapping> temp = new ArrayList<>();
                    for (Object obj : jsonArray) {
                        maps.forEach(m -> {
                            SolutionMapping copy = new SolutionMapping(m);
                            copy.put(f.name(), new LiteralNode(obj.toString(), XSDDatatype.XSDstring));
                            temp.add(copy);
                        });
                    }
                    maps = temp;
                } else { // a JSON value
                    maps.forEach(map -> map.put(f.name(), new LiteralNode(value.toString(), XSDDatatype.XSDstring)));
                }
            } else { // value not ok, put a null
                maps.forEach(map -> map.put(f.name(), new NullNode()));
            }
        }
        solutionMappingQueue.addAll(maps);
    }


    @Override
    @NonNull
    protected MappingTuple nextEffective() {
        if (solutionMappingQueue.isEmpty() && this.sourceIterator.hasNext()) {
            queueNextSolutionMappings();
        }
        MappingTuple tuple = new MappingTuple();
        SolutionMapping map = solutionMappingQueue.poll();
        tuple.addSolutionMap(this.defaultFragment, map);
        return tuple;

    }


    @Override
    public boolean hasNext() {
        // No need to check if source iterator is null, since this would throw an exception during the init.
        return this.isReady() && (!this.solutionMappingQueue.isEmpty() || (this.sourceIterator.hasNext()));
    }

    @Override
    public void init() {
        try {
            this.sourceIterator = new JSONSourceIterator(this.access, this.rootIterator);
            this.setReady(true);
        } catch (Exception e) {
            this.setReady(false);
            throw new RuntimeException(e);
        }
    }
}

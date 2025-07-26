package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.record.JSONRecord;
import net.minidev.json.JSONObject;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.jspecify.annotations.NonNull;

import java.util.*;


/**
 * Implementation of the SourceOperator using DataIO for JSON sources.
 * DataIO is written with infinite sources in mind: it provides no way to
 * inspect all variables present in the stream.
 * For this reason, this source must be provided with variables to be read and
 * included in the MappingTuple
 */
public class JSONSourceOperator extends DataIOSourceOperator {

    private final String rootIterator;
    private final Deque<SolutionMapping> solutionMappingQueue = new ArrayDeque<>();
    private transient JSONSourceIterator sourceIterator;

    public JSONSourceOperator(String operatorName, Access access, String defaultFragment,
                              String rootIterator,
                              List<Field> fields) {
        super(operatorName, access, defaultFragment, fields);
        this.rootIterator = rootIterator;
        this.sourceIterator = null;
    }

    @Override
    @NonNull
    public MappingTuple consumeSource() {
        MappingTuple tuple = new MappingTuple();
        try {
            this.init();
            while (this.sourceIterator.hasNext()) {
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

    private void queueNextSolutionMappings() {
        JSONRecord r = (JSONRecord) sourceIterator.next();
        JSONObject json = new JSONObject((Map<String, ?>) r.get("$").getValue());

        List<SolutionMapping> mappings = new ArrayList<>();

        for (Field f : this.fields) {
            List<SolutionMapping> fieldMaps = f.apply(json.toJSONString());

            if (mappings.isEmpty()) {
                mappings.addAll(fieldMaps);
            } else {

                List<SolutionMapping> newMaps = new ArrayList<>();

                for (SolutionMapping m : mappings) {
                    for (SolutionMapping fieldMap : fieldMaps) {
                        newMaps.add(m.union(fieldMap));
                    }
                }

                mappings = new ArrayList<>(newMaps);
            }
        }

        mappings.forEach(m -> m.put("#", new LiteralNode(r.getIndex(), XSDDatatype.XSDinteger)));

        solutionMappingQueue.addAll(mappings);
    }

    private RDFNode getLiteralNode(Object value) {
        if (value instanceof Number) {
            return new LiteralNode(value, XSDDatatype.XSDinteger);
        }

        return new LiteralNode(value);
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

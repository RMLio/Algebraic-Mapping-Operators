package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.record.Record;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import java.util.Collection;
import java.util.List;

/**
 * Implementation of the SourceOperator using DataIO for JSON sources.
 * DataIO is written with infinite sources in mind: it provides no way to
 * inspect all variables present in the stream.
 * For this reason, this source must be provided with variables to be read and
 * included in the MappingTuple
 */
public class JSONSourceOperator extends DataIOSourceOperator {

    private final Collection<String> rootVariables;
    private final String rootIterator;
    private final Collection<String> subIterators;
    private transient JSONSourceIterator sourceIterator;

    public JSONSourceOperator(String operatorName, Access access, Collection<String> rootVariables, String rootIterator,
            Collection<String> subIterators) {
        this(operatorName, access, "default", rootVariables, rootIterator, subIterators);
    }

    public JSONSourceOperator(String operatorName, Access access, String defaultFragment,
            Collection<String> rootVariables, String rootIterator,
            Collection<String> subIterators) {
        super(operatorName, access, defaultFragment);
        this.rootVariables = rootVariables;
        this.rootIterator = rootIterator;
        this.subIterators = subIterators;
        this.sourceIterator = null;
    }

    @Override
    public MappingTuple consumeSource() {
        MappingTuple tuple = new MappingTuple();

        // TODO consider caching the Access stream
        // get everything from rootIterator
        try {
            this.init();
            JSONSourceIterator iterator = this.sourceIterator;
            while (iterator.hasNext()) {
                Record r = iterator.next();
                SolutionMapping map = new SolutionMapping();
                // consume variables to be fetched from the root iterator
                consumeRecord(r, this.rootVariables, map);
                // consume any and all subiterators with respect to the root iterator
                consumeRecord(r, this.subIterators, map);

                tuple.addSolutionMap(this.defaultFragment, map);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return tuple;
    }

    private void consumeRecord(Record r, Collection<String> iterators, SolutionMapping map) {
        for (String it : iterators) {
            List<Object> values = r.get(it);
            String value;
            if (!values.isEmpty()) {
                value = (String) values.get(0);
            } else {
                value = "";
            }
            map.put(it, new LiteralNode(value, XSDDatatype.XSDstring));
        }
    }

    @Override
    protected MappingTuple nextEffective() {
        MappingTuple tuple = new MappingTuple();
        Record r = this.sourceIterator.next();
        SolutionMapping map = new SolutionMapping();
        // consume variables to be fetched from the root iterator
        consumeRecord(r, this.rootVariables, map);
        // consume any and all subiterators with respect to the root iterator
        consumeRecord(r, this.subIterators, map);
        tuple.addSolutionMap(this.defaultFragment, map);

        return tuple;

    }

    @Override
    public boolean hasNext() {
        return this.isReady() && this.sourceIterator != null && this.sourceIterator.hasNext();
    }

    @Override
    public void init() throws Exception {
        try {
            JSONSourceIterator iterator = new JSONSourceIterator(this.access, this.rootIterator);
            this.sourceIterator = iterator;
            this.setReady(true);
        } catch (Exception e) {
            this.setReady(false);
            throw new RuntimeException(e);

        }
    }
}

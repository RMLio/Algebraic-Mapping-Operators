package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.record.Record;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import java.io.ObjectInputStream;
import java.io.Serial;
import java.util.Collection;
import java.util.List;

/**
 * Implementation of the SourceOperator using DataIO for JSON sources.
 * DataIO is written with infinite sources in mind: it provides no way to inspect all variables present in the stream.
 * For this reason, this source must be provided with variables to be read and included in the MappingTuple
 */
public class JSONSourceOperator extends DataIOSourceOperator {

    private  Collection<String> rootVariables;
    private  String rootIterator;
    private  Collection<String> subIterators;

    public JSONSourceOperator(Access access, Collection<String> rootVariables, String rootIterator,
                              Collection<String> subIterators) {
        super(access);
        this.rootVariables = rootVariables;
        this.rootIterator = rootIterator;
        this.subIterators = subIterators;
    }

    @Override
    public MappingTuple consumeSource() {
        MappingTuple tuple = new MappingTuple();

        // TODO consider caching the Access stream
        // get everything from rootIterator
        try (JSONSourceIterator iterator = new JSONSourceIterator(this.access, this.rootIterator)) {
            while (iterator.hasNext()) {
                Record r = iterator.next();
                SolutionMapping map = new SolutionMapping();
                // consume variables to be fetched from the root iterator
                consumeRecord(r, this.rootVariables, map);
                // consume any and all subiterators with respect to the root iterator
                consumeRecord(r, this.subIterators, map);

                tuple.addSolutionMap("f_default", map);
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
            map.put("?" + it, new LiteralNode(value, XSDDatatype.XSDstring));
        }
    }


}

package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.dataio.record.Record;
import be.ugent.idlab.knows.dataio.record.XMLRecord;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.NoSuchElementException;

public class XMLSourceOperator extends DataIOSourceOperator {
    private final String rootIterator;
    private transient XMLSourceIterator sourceIterator;

    public XMLSourceOperator(String operatorName, Access access, String defaultFragment,
                             String rootIterator, List<Field> fields) {
        super(operatorName, access, defaultFragment, fields);
        this.rootIterator = rootIterator;
        this.sourceIterator = null;
    }

    private List<SolutionMapping> consumeRecord(Record r) {
//        for (String it : iterators) {
//            RecordValue recordValue = r.get(it);
//
//            if (recordValue.isOk()) {
//                // when RecordValue is ok, the result of the iterator is a list
//                //noinspection unchecked
//                List<String> value = (List<String>) recordValue.getValue();
//                if (value.size() == 1) {
//                    mapping.put(it, new LiteralNode(value.getFirst(), XSDDatatype.XSDstring));
//                } else {
//                    mapping.put(it, new LiteralNode(recordValue.getValue().toString(), XSDDatatype.XSDstring));
//                }
//            } else {
//                mapping.put(it, new NullNode());
//            }
//        }
        return null;
    }

    @Override
    @NonNull
    protected MappingTuple nextEffective() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }

        XMLRecord r = (XMLRecord) this.sourceIterator.next();
        List<SolutionMapping> sms = applySubfields(r.getItem().toString(), r.getIndex());

        MappingTuple out = new MappingTuple();
        out.setSolutionMaps(this.defaultFragment, sms);

        return out;
    }

    @Override
    public boolean hasNext() {
        if (!this.isReady()) {
            this.init();
        }

        return this.isReady() && this.sourceIterator != null && this.sourceIterator.hasNext();
    }

    @Override
    public void init() {
        if (this.isReady()) {
            return;
        }

        try {
            this.sourceIterator = new XMLSourceIterator(this.access, this.rootIterator);
            this.setReady(true);
        } catch (Exception e) {
            this.setReady(false);
            throw new RuntimeException(e);

        }
    }
}

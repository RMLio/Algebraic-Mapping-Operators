package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.record.JSONRecord;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * Implementation of the SourceOperator using DataIO for JSON sources.
 */
public class JSONSourceOperator extends DataIOSourceOperator {
    private final String rootIterator;
    private transient JSONSourceIterator sourceIterator;

    public JSONSourceOperator(String operatorName, Access access, String defaultFragment,
                              String rootIterator,
                              List<Field> fields,
                              List<String> nulls) {
        super(operatorName, access, defaultFragment, fields, nulls);
        this.rootIterator = rootIterator;
        this.sourceIterator = null;
    }

    @Override
    @NonNull
    protected MappingTuple nextEffective() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }

        JSONRecord r = (JSONRecord) this.sourceIterator.next();

        JSONArray outputArray = (JSONArray) r.get("$").getValue();
        JSONObject json = new JSONObject((Map<String, ?>) outputArray.getFirst());
        List<SolutionMapping> mappings = applySubfields(json.toJSONString(), r.getIndex());

        MappingTuple out = new MappingTuple();
        out.setSolutionMaps(this.defaultFragment, mappings);

        return out;
    }

    @Override
    public boolean hasNext() {
        if (!this.isReady()) {
            this.init();
        }

        // No need to check if source iterator is null, since this would throw an exception during the init.
        return this.isReady() && this.sourceIterator.hasNext();
    }

    @Override
    public void init() {
        if (this.isReady()) {
            return;
        }

        try {
            this.sourceIterator = new JSONSourceIterator(this.access, this.rootIterator);
            this.setReady(true);
        } catch (Exception e) {
            this.setReady(false);
            throw new RuntimeException(e);
        }
    }
}

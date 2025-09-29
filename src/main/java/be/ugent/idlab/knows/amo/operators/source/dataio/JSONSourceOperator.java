package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.record.JSONRecord;
import net.minidev.json.JSONObject;
import org.jspecify.annotations.NonNull;

import java.util.*;


/**
 * Implementation of the SourceOperator using DataIO for JSON sources.
 */
public class JSONSourceOperator extends DataIOSourceOperator {
    private final String rootIterator;
    private transient JSONSourceIterator sourceIterator;

    public JSONSourceOperator(String operatorName, Access access, Set<String> outputFragments,
                              String rootIterator,
                              Collection<Field> fields,
                              Collection<String> nulls) {
        super(operatorName, access, outputFragments, fields, nulls);
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

        Map<String, ?> output = (Map<String, ?>) r.get("$").getValue();
        JSONObject json = new JSONObject(output);
        List<SolutionMapping> mappings = applyFields(json.toJSONString(), r.getIndex());

        MappingTuple out = new MappingTuple();
        for (String outputFragment : getOutputFragments()) {
            out.setSolutionMaps(outputFragment, mappings);
        }

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

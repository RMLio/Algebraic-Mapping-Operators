package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.dataio.access.VirtualAccess;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.jspecify.annotations.Nullable;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The variables of a single raw record, as seen by an expression computing a field's
 * value (e.g. {@code toUpperCase(name)}).
 * <p>
 * The record's own attributes are bound up front so that the binding can be listed (an
 * expression asking for an attribute that is not there reports what is available). An
 * attribute that is not among them is resolved lazily as a reference into the record,
 * which is what makes a path-shaped variable (a JSONPath, an XPath) work as well as a
 * plain name. Resolved values are cached, including their absence.
 */
class RecordBinding extends SolutionMapping {

    private final transient Optional<String> record;
    private final transient RecordReader reader;
    private final ReferenceFormulation referenceFormulation;

    RecordBinding(Optional<String> record, RecordReader reader) {
        this.record = record;
        this.reader = reader;
        this.referenceFormulation = reader.referenceFormulation();

        bindRecordAttributes();
    }

    /**
     * Binds the record's own attributes: the columns of a CSV record, the members of a
     * JSON object. An XML record has no such flat list of attributes; its variables are
     * resolved lazily instead.
     */
    private void bindRecordAttributes() {
        if (this.record.isEmpty()) {
            return;
        }

        switch (this.referenceFormulation) {
            case CSVRows -> bindCSVColumns(this.record.get());
            case JSONPath -> bindJSONMembers(this.record.get());
            case XMLPath -> {
                // resolved lazily, see get(Object)
            }
        }
    }

    private void bindCSVColumns(String record) {
        VirtualAccess access = new VirtualAccess(record.getBytes(Charset.defaultCharset()));

        try (CSVSourceIterator iterator = new CSVSourceIterator(access)) {
            while (iterator.hasNext()) {
                CSVRecord r = (CSVRecord) iterator.next();
                for (Map.Entry<String, String> entry : r.getData().entrySet()) {
                    super.put(entry.getKey(), Field.getLiteralNode(entry.getValue()));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void bindJSONMembers(String record) {
        Object read;
        try {
            read = JsonPath.read(record, "$");
        } catch (PathNotFoundException e) {
            return;
        }

        if (read instanceof Map<?, ?> members) {
            for (Map.Entry<?, ?> entry : members.entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();
                // a nested object or array is not a variable an expression can read; it is
                // reached through a path, which get(Object) resolves lazily
                if (key != null && !(value instanceof Map<?, ?>) && !(value instanceof List<?>)) {
                    super.put(key.toString(), Field.getLiteralNode(value));
                }
            }
        }
    }

    @Override
    @Nullable
    public RDFNode get(@Nullable Object key) {
        if (key == null) {
            return null;
        }

        if (!super.containsKey(key)) {
            resolve(key.toString());
        }

        return super.get(key);
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        if (key == null) {
            return false;
        }

        if (!super.containsKey(key)) {
            resolve(key.toString());
        }

        return super.get(key) != null;
    }

    /**
     * Reads the attribute out of the record and caches the outcome, binding {@code null}
     * when the record holds no such attribute so that it is read only once.
     */
    private void resolve(String key) {
        List<Object> values = this.reader.read(this.record, key);
        super.put(key, values.isEmpty() ? null : Field.getLiteralNode(values.get(0)));
    }
}

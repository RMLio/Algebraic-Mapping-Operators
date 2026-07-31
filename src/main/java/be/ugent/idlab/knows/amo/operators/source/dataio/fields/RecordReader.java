package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.dataio.access.VirtualAccess;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.SourceIterator;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import be.ugent.idlab.knows.dataio.record.RecordValue;
import be.ugent.idlab.knows.dataio.record.XMLRecord;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import net.minidev.json.JSONArray;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Reads attributes out of the records of one field, following the field's reference
 * formulation: a column for CSV, a JSONPath for JSON, an XPath for XML.
 * <p>
 * A path may match several values (a JSON array, an XML node list), so reading always
 * yields a list. This is the only place that knows how to follow a path into raw data;
 * both {@link ExpressionField} (for a bare reference) and {@link RecordBinding} (for the
 * variables a computed expression reads) go through it.
 * <p>
 * A reader belongs to a single field and keeps the source iterators it reads with between
 * records. Reading a record is cheaper than setting one up, so a field that reads many
 * records would otherwise spend most of its time building parsers it already had. This
 * makes a reader stateful, so it cannot be shared between threads.
 */
final class RecordReader {

    static {
        Configuration.setDefaults(new Configuration.Defaults() {
            private final JsonProvider jsonProvider = Configuration.defaultConfiguration().jsonProvider();
            private final MappingProvider mappingProvider = Configuration.defaultConfiguration().mappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public Set<Option> options() {
                return Set.of(Option.DEFAULT_PATH_LEAF_TO_NULL, Option.REQUIRE_PROPERTIES);
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }
        });
    }

    /** The key under which the CSV iterator is kept: it reads rows whatever the reference. */
    private static final String CSV_ITERATOR = "";

    private final ReferenceFormulation referenceFormulation;
    private final String prefix;
    private final Map<String, SourceIterator> iterators = new HashMap<>();

    RecordReader(ReferenceFormulation referenceFormulation, String prefix) {
        this.referenceFormulation = referenceFormulation;
        this.prefix = prefix;
    }

    /**
     * A JSONPath segment containing a space has to be bracketed to be a valid path.
     *
     * @param reference the reference to normalize
     * @return the reference, bracketed if needed
     */
    static String normalize(String reference, ReferenceFormulation referenceFormulation) {
        if (referenceFormulation == ReferenceFormulation.JSONPath && reference != null
                && !reference.startsWith("[") && !reference.startsWith("$") && reference.contains(" ")) {
            return "['%s']".formatted(reference);
        }

        return reference;
    }

    ReferenceFormulation referenceFormulation() {
        return this.referenceFormulation;
    }

    /**
     * Reads the given reference out of the record.
     *
     * @param record    the raw record, empty if there is no record to read
     * @param reference the attribute to read
     * @return the values the reference matches, empty if it matches nothing
     */
    List<Object> read(Optional<String> record, String reference) {
        if (record.isEmpty() || reference == null) {
            return List.of();
        }

        String path = resolve(reference);

        return switch (this.referenceFormulation) {
            case CSVRows -> readCSV(record.get(), path);
            case JSONPath -> readJSON(record.get(), normalize(path, this.referenceFormulation));
            case XMLPath -> readXML(record.get(), path).stream().map(XMLValue::text).map(Object.class::cast).toList();
        };
    }

    /**
     * An XML element the reference matched: its text, which is the field's value, and its
     * markup, which is what subfields read.
     *
     * @param text    the element's text content
     * @param element the element as XML, so that an XPath can be applied to it again
     */
    record XMLValue(String text, String element) {
    }

    /**
     * Reads the elements the given reference matches, keeping each element's markup next
     * to its text.
     * <p>
     * A field binds an element's text, but its subfields have to read the element itself:
     * an XPath cannot be applied to text content. This mirrors what
     * {@link IteratorField} hands its subfields.
     *
     * @param record    the raw record, empty if there is no record to read
     * @param reference the attribute to read
     * @return the elements matched, empty if the reference matches nothing
     */
    List<XMLValue> readXMLValues(Optional<String> record, String reference) {
        if (record.isEmpty() || reference == null) {
            return List.of();
        }

        return readXML(record.get(), resolve(reference));
    }

    private String resolve(String reference) {
        return this.prefix == null ? reference : "%s/%s".formatted(this.prefix, reference);
    }

    /**
     * The iterator reading the given path, built on first use and pointed at every record
     * read after that.
     */
    private SourceIterator iteratorFor(String key, VirtualAccess access, IteratorBuilder builder) {
        try {
            SourceIterator iterator = this.iterators.get(key);
            if (iterator == null) {
                iterator = builder.build(access);
                this.iterators.put(key, iterator);
            } else {
                iterator.reset(access);
            }

            return iterator;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Object> readCSV(String record, String reference) {
        VirtualAccess access = new VirtualAccess(record.getBytes(Charset.defaultCharset()));
        List<Object> out = new ArrayList<>();

        SourceIterator iterator = iteratorFor(CSV_ITERATOR, access, CSVSourceIterator::new);
        while (iterator.hasNext()) {
            CSVRecord r = (CSVRecord) iterator.next();
            RecordValue rv = r.get(reference);
            if (rv.isOk()) {
                out.add(rv.getValue());
            }
        }

        return out;
    }

    private List<Object> readJSON(String record, String reference) {
        Object read;
        try {
            // no path caching here on purpose: JsonPath compiles through its own cache
            // (CacheProvider), and caching again measured slightly slower
            Object readObject = JsonPath.read(record, reference);

            if (readObject instanceof JSONArray arr) {
                if (!arr.isEmpty() && !reference.contains("[*]")) {
                    throw new IllegalArgumentException("Reference field reading an array without iteration");
                }
                read = arr;
            } else {
                read = readObject;
            }
        } catch (PathNotFoundException ex) {
            return List.of();
        }

        if (read == null) {
            return List.of();
        }

        if (read instanceof List<?> list) {
            return new ArrayList<>(list);
        }

        return List.of(read);
    }

    private List<XMLValue> readXML(String record, String reference) {
        VirtualAccess access = new VirtualAccess(record.getBytes(Charset.defaultCharset()));

        SourceIterator iterator = iteratorFor(reference, access, a -> new XMLSourceIterator(a, reference));
        List<XMLValue> out = new ArrayList<>();
        while (iterator.hasNext()) {
            XMLRecord r = (XMLRecord) iterator.next();
            RecordValue recordValue = r.get(".");
            if (recordValue.isOk()) {
                String element = r.getItem().toString();
                for (String text : (List<String>) recordValue.getValue()) {
                    out.add(new XMLValue(text, element));
                }
            }
        }

        return out;
    }

    @FunctionalInterface
    private interface IteratorBuilder {
        SourceIterator build(VirtualAccess access) throws Exception;
    }
}

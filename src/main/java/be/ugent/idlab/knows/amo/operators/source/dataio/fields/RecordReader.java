package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.dataio.access.VirtualAccess;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Reads an attribute out of a single raw record, following the record's reference
 * formulation: a column for CSV, a JSONPath for JSON, an XPath for XML.
 * <p>
 * A path may match several values (a JSON array, an XML node list), so reading always
 * yields a list. This is the only place that knows how to follow a path into raw data;
 * both {@link ExpressionField} (for a bare reference) and {@link RecordBinding} (for the
 * variables a computed expression reads) go through it.
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

    private RecordReader() {
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

    /**
     * Reads the given reference out of the record.
     *
     * @param record                 the raw record, empty if there is no record to read
     * @param reference              the attribute to read
     * @param referenceFormulation   how the record, and hence the reference, is to be read
     * @param prefix                 the path the reference is relative to, {@code null} if it is absolute
     * @return the values the reference matches, empty if it matches nothing
     */
    static List<Object> read(Optional<String> record, String reference,
                             ReferenceFormulation referenceFormulation, String prefix) {
        if (record.isEmpty() || reference == null) {
            return List.of();
        }

        String path = prefix == null ? reference : "%s/%s".formatted(prefix, reference);

        return switch (referenceFormulation) {
            case CSVRows -> readCSV(record.get(), path);
            case JSONPath -> readJSON(record.get(), normalize(path, referenceFormulation));
            case XMLPath -> readXML(record.get(), path);
        };
    }

    private static List<Object> readCSV(String record, String reference) {
        VirtualAccess access = new VirtualAccess(record.getBytes(Charset.defaultCharset()));
        List<Object> out = new ArrayList<>();

        try (CSVSourceIterator iterator = new CSVSourceIterator(access)) {
            while (iterator.hasNext()) {
                CSVRecord r = (CSVRecord) iterator.next();
                RecordValue rv = r.get(reference);
                if (rv.isOk()) {
                    out.add(rv.getValue());
                }
            }

            return out;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Object> readJSON(String record, String reference) {
        Object read;
        try {
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

    private static List<Object> readXML(String record, String reference) {
        VirtualAccess access = new VirtualAccess(record.getBytes(Charset.defaultCharset()));

        try (XMLSourceIterator iterator = new XMLSourceIterator(access, reference)) {
            List<Object> out = new ArrayList<>();
            while (iterator.hasNext()) {
                XMLRecord r = (XMLRecord) iterator.next();
                RecordValue recordValue = r.get(".");
                if (recordValue.isOk()) {
                    out.addAll((List<String>) recordValue.getValue());
                }
            }
            return out;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

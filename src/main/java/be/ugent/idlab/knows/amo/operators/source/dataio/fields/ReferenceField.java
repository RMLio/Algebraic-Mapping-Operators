package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
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
import net.minidev.json.JSONObject;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.*;

public class ReferenceField extends Field {

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

    private final String reference;

    public ReferenceField(String name, Collection<Field> subfields, ReferenceFormulation referenceFormulation, String reference) {
        super(name, subfields, referenceFormulation);
        if (referenceFormulation == ReferenceFormulation.JSONPath) {
            if ((!reference.startsWith("[") && !reference.startsWith("$")) && reference.contains(" ")) {
                reference = "['%s']".formatted(reference);
            }
        }
        this.reference = reference;
    }

    @Override
    public List<SolutionMapping> apply(Optional<String> obj) {
        List<Object> values = switch (this.referenceFormulation) {
            case CSVRows -> processCSV(obj);
            case JSONPath -> processJSON(obj);
            case XMLPath -> processXML(obj);
        };

        List<SolutionMapping> out = new ArrayList<>();

        if (values.isEmpty()) {
            out.add(new SolutionMapping(Map.of(
                    this.name, getLiteralNode(null),
                    this.name + ".#", getLiteralNode(0))
            ));

            return out;
        }

        for (int i = 0; i < values.size(); i++) {
            Object o = values.get(i);

            String sub;
            if (o instanceof Map<?, ?>) {
                sub = new JSONObject((Map<String, Object>) o).toJSONString();
            } else if (o == null) {
                sub = null;
            } else {
                sub = o.toString();
            }

            Collection<SolutionMapping> subfieldMaps = this.applySubfields(Optional.of(sub));
            for (SolutionMapping sm : subfieldMaps) {
                // extend keys with field's name
                for (String key : new HashSet<>(sm.keySet())) {
                    sm.put(this.name + "." + key, sm.get(key));
                    sm.remove(key);
                }
            }

            if (subfieldMaps.isEmpty()) {
                out.add(new SolutionMapping(Map.of(
                        this.name, getLiteralNode(o),
                        this.name + ".#", getLiteralNode(i)
                )));
            } else {
                for (SolutionMapping sm : subfieldMaps) {
                    sm.put(this.name, getLiteralNode(o));
                    sm.put(this.name + ".#", getLiteralNode(i));
                }
            }

            out.addAll(subfieldMaps);
        }

        return out;
    }

    private List<Object> processXML(Optional<String> input_obj) {
        if (input_obj.isEmpty()){
            return List.of();
        }
        String obj = input_obj.get();
        VirtualAccess access = new VirtualAccess(obj.getBytes(Charset.defaultCharset()));

        try (XMLSourceIterator iterator = new XMLSourceIterator(access, this.reference)) {
            List<Object> out = new ArrayList<>();
            while (iterator.hasNext()) {
                XMLRecord r = (XMLRecord) iterator.next();
                List<String> value = (List<String>) r.get(".").getValue();
                out.add(value.getFirst());
            }
            return out;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Object> processJSON(Optional<String> input_obj) {
        if (input_obj.isEmpty()){
            return List.of();
        }

        Object read;
        String obj = input_obj.get();
        try {
            Object readObject = JsonPath.read(obj, this.reference);

            if (readObject instanceof JSONArray arr) {
                if (!arr.isEmpty() /*&& arr.getFirst() instanceof JSONArray*/ && !this.reference.contains("[*]")) {
                    throw new IllegalArgumentException("Reference field reading an array without iteration");
                }
                read = arr;
//                if (!this.reference.contains("[*]")) {
//                    read = arr.getFirst();
//                } else {
//                    read = arr;
//                }
            } else {
                read = readObject;
            }

        } catch (PathNotFoundException ex) {
            return List.of();
        }

        if (read == null) {
            return List.of();
        }

        if (read instanceof ArrayList<?>) {
            return (List<Object>) read;
        }

        return List.of(read);
    }

    public String getReference() {
        return reference;
    }

    private List<Object> processCSV(Optional<String> input_obj) {
        if (input_obj.isEmpty()){
            return List.of();
        }
        String obj = input_obj.get();
        VirtualAccess access = new VirtualAccess(obj.getBytes(Charset.defaultCharset()));
        List<Object> out = new ArrayList<>();
        try (CSVSourceIterator iterator = new CSVSourceIterator(access)){
            while (iterator.hasNext()) {
                CSVRecord r = (CSVRecord) iterator.next();
                RecordValue rv = r.get(this.reference);
                if (rv.isOk()) {
                    out.add(rv.getValue());
                } else {
                    out.add(null);
                }
            }

            return out;
        } catch (SQLException | IOException | ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "RefField[name=%s,reference=%s,subfields=%s]".formatted(this.name, this.reference, this.subfields);
    }
}

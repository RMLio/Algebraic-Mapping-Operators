package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.dataio.access.VirtualAccess;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.dataio.record.XMLRecord;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.opencsv.CSVReader;
import net.minidev.json.JSONObject;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.*;

public class ReferenceField extends Field {
    private final String reference;

    public ReferenceField(String name, Collection<Field> subfields, ReferenceFormulation referenceFormulation, String reference) {
        super(name, subfields, referenceFormulation);
        this.reference = reference;
    }

    @Override
    public List<SolutionMapping> apply(String obj) {
        List<Object> values = switch (this.referenceFormulation) {
            case CSVRows -> processCSV(obj);
            case JSONPath -> processJSON(obj);
            case XPath -> processXML(obj);
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
            } else {
                sub = o.toString();
            }

            Collection<SolutionMapping> subfieldMaps = this.applySubfields(sub);
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

    private List<Object> processXML(String obj) {
        VirtualAccess access = new VirtualAccess(obj.getBytes(Charset.defaultCharset()));

        XMLSourceIterator iterator;
        try {
             iterator = new XMLSourceIterator(access, this.reference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Object> out = new ArrayList<>();
        while (iterator.hasNext()) {
            XMLRecord r = (XMLRecord) iterator.next();
            out.add(r.get("."));
        }
        return out;
    }


    private List<Object> processJSON(String obj) {

        Object read;
        try {
            read = JsonPath.read(obj, this.reference);
        } catch (PathNotFoundException ex) {
            // the value not being found results in a null node
            return List.of();
        }

        if (read == null) {
            return List.of();
        }

        if (read instanceof ArrayList<?>) {
            return (List<Object>) read;
        }

        return List.of(read);

//        return JsonPath.read(obj, this.reference);
    }

    public String getReference() {
        return reference;
    }

    private List<Object> processCSV(String obj) {
        CSVReader reader = new CSVReader(new StringReader(obj));
        // assume first line always contains the header
        Iterator<String[]> readerIterator = reader.iterator();

        String[] header = readerIterator.next();
        int iteratorIndex = -1;
        for (int i = 0; i < header.length; i++) {
            if (header[i].equals(this.reference)) {
                iteratorIndex = i;
                break;
            }
        }

        if (iteratorIndex < 0) {
            throw new IllegalStateException("Iterator %s not part of data %s".formatted(this.reference, obj));
        }

        List<Object> objects = new ArrayList<>();
        while (readerIterator.hasNext()) {
            String[] data = readerIterator.next();
            objects.add(data[iteratorIndex]);
        }

        return objects;
    }

    @Override
    public String toString() {
        return "RefField[name=%s,reference=%s,subfields=%s]".formatted(this.name, this.reference, this.subfields);
    }
}

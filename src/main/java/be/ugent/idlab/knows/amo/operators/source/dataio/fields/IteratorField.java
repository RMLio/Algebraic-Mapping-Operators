package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.dataio.access.VirtualAccess;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.dataio.record.XMLRecord;
import com.jayway.jsonpath.JsonPath;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.*;

public class IteratorField extends Field {

    private final String iterator;

    public IteratorField(String name, Collection<Field> subfields, ReferenceFormulation referenceFormulation, String iterator) {
        super(name, subfields, referenceFormulation);

        if (iterator != null && !iterator.startsWith("[") && iterator.contains(" ")) {
            iterator = "['%s']".formatted(iterator);
        }

        this.iterator = iterator;
    }

    @Override
    public List<SolutionMapping> apply(String obj) {
        List<SolutionMapping> applied = switch (this.referenceFormulation) {
            case CSVRows -> processCSV(obj);
            case JSONPath -> processJSON(obj);
            case XMLPath -> processXML(obj);
        };

        for (SolutionMapping sm : applied) {
            for (String key : new HashSet<>(sm.keySet())) {
                sm.put(this.name + "." + key, sm.get(key));
                sm.remove(key);
            }
        }

        for (int i = 0; i < applied.size(); i++) {
            SolutionMapping sm = applied.get(i);
            sm.put(this.name + ".#", new LiteralNode(i, XSDDatatype.XSDinteger));
        }


        return applied;
    }

    private List<SolutionMapping> processXML(String obj) {
        VirtualAccess access = new VirtualAccess(obj.getBytes(Charset.defaultCharset()));
        XMLSourceIterator xmlIterator;

        String iterator = this.iterator == null  ? "." :  this.iterator;

        try {
            xmlIterator = new XMLSourceIterator(access, iterator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<SolutionMapping> result = new ArrayList<>();

        while (xmlIterator.hasNext()) {
            XMLRecord r =  (XMLRecord) xmlIterator.next();
            String sub = r.getItem().toString();
            result.addAll(applySubfields(sub));
        }

        return result;
    }

    private List<SolutionMapping> processJSON(String obj) {
        Object readObject = JsonPath.read(obj, this.iterator);

        String sub;

        List<SolutionMapping> out = new ArrayList<>();

        if (readObject instanceof JSONArray array) {
            for (int i = 0; i < array.size(); i++) {
                Object value = array.get(i);
                if (value instanceof LinkedHashMap<?, ?> map) {
                    sub = new JSONObject((Map<String, Object>) map).toJSONString();
                } /*else if (value instanceof JSONArray arr) {
                List<SolutionMapping> result = new ArrayList<>();
                for (Object o : arr) {
                    JSONObject jsonObject = new JSONObject((Map<String, Object>) o);
                    result.addAll(applySubfields(jsonObject.toJSONString()));
                }

                return result;
            } */ else {
                    sub = value.toString();
                }

                out.addAll(applySubfields(sub));

//            return new ArrayList<>(applySubfields(sub));
            }
        } else if (readObject instanceof LinkedHashMap<?, ?> map) {
            sub = new JSONObject((Map<String, Object>) map).toJSONString();
            out.addAll(applySubfields(sub));
        }

        return out;
    }

    private List<SolutionMapping> processCSV(String obj) {
        List<SolutionMapping> out = new ArrayList<>();

        CSVReader csvReader = new CSVReader(new StringReader(obj));

        if (this.iterator != null) {
            // extract the value out of the object
            try {
                String[] header = csvReader.readNext();

                int iteratorIndex = -1;
                // find the index of the iterator
                for (int i = 0; i < header.length; i++) {
                    if (header[i].equals(this.iterator)) {
                        iteratorIndex = i;
                        break;
                    }
                }

                if (iteratorIndex == -1) {
                    throw new IllegalArgumentException("Supplied CSV content does not contain a field %s".formatted(this.name));
                }

                // extract the value and process next fields on it
                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    String value = line[iteratorIndex];
                    out.addAll(applySubfields(value));
                }
            } catch (IOException | CsvValidationException e) {
                throw new RuntimeException(e);
            }
        } else {
            // do not extract the value, simply apply all fields
            try {
                String[] header = csvReader.readNext();

                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    StringWriter sw = new StringWriter();
                    CSVWriter csvWriter = new CSVWriter(sw);

                    csvWriter.writeAll(List.of(header, line));
                    csvWriter.close();

                    out.addAll(applySubfields(sw.toString()));
                }
            } catch (CsvValidationException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        return out;
    }

    public String getIterator() {
        return iterator;
    }

    @Override
    public String toString() {
        return "IteratorField[name=%s,iterator=%s,subfields=%s]".formatted(this.name, this.iterator, this.subfields);
    }
}

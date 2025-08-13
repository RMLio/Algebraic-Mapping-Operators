package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.dataio.access.VirtualAccess;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import be.ugent.idlab.knows.dataio.record.XMLRecord;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
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
            XMLRecord r = (XMLRecord) xmlIterator.next();
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
            for (Object value : array) {
                if (value instanceof LinkedHashMap<?, ?> map) {
                    sub = new JSONObject((Map<String, Object>) map).toJSONString();
                } else {
                    sub = value.toString();
                }

                out.addAll(applySubfields(sub));

            }
        } else if (readObject instanceof LinkedHashMap<?, ?> map) {
            sub = new JSONObject((Map<String, Object>) map).toJSONString();
            out.addAll(applySubfields(sub));
        }

        return out;
    }

    private List<SolutionMapping> processCSV(String obj) {

        VirtualAccess access = new VirtualAccess(obj.getBytes(Charset.defaultCharset()));
        List<SolutionMapping> out = new ArrayList<>();

        try {
            CSVSourceIterator iterator = new CSVSourceIterator(access);
            while (iterator.hasNext()) {
                CSVRecord r = (CSVRecord) iterator.next();

                String value;
                if (this.iterator != null) {
                    value = r.getData().get(this.iterator);
                } else {
                    // package the record
                    value = r.toCSVString();
                }
                out.addAll(applySubfields(value));
            }

            return out;
        } catch (SQLException | IOException | ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public String getIterator() {
        return iterator;
    }

    @Override
    public String toString() {
        return "IteratorField[name=%s,iterator=%s,subfields=%s]".formatted(this.name, this.iterator, this.subfields);
    }
}

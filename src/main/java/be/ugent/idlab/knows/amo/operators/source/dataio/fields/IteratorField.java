package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.dataio.access.VirtualAccess;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.SourceIterator;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import be.ugent.idlab.knows.dataio.record.Record;
import be.ugent.idlab.knows.dataio.record.RecordValue;
import be.ugent.idlab.knows.dataio.record.XMLRecord;
import com.fasterxml.jackson.databind.node.TextNode;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class IteratorField extends Field {

    private final String iterator;

    /**
     * The iterator over the object being read, kept between objects.
     * <p>
     * Setting a source up costs more than reading it: the iterator's expression has to be
     * compiled and a parser built. This field's reference formulation and iterator
     * expression never change, so one source iterator is built and pointed at each object
     * in turn rather than built per object.
     * <p>
     * It is transient because it holds a parser over the object being read; after
     * deserialization it is built again on first use. It also makes this field stateful
     * while it reads, so a field cannot be applied from two threads at once.
     */
    private transient SourceIterator sourceIterator;

    public IteratorField(String name, Collection<Field> subfields, ReferenceFormulation referenceFormulation, String iterator) {
        super(name, subfields, referenceFormulation);

        if (iterator != null && !iterator.startsWith("[") && iterator.contains(" ")) {
            iterator = "['%s']".formatted(iterator);
        }

        this.iterator = iterator;
    }

    @Override
    public List<SolutionMapping> apply(Optional<String> obj) {
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

    /**
     * The iterator over the given object: built on first use, and pointed at every object
     * read after that.
     *
     * @param obj the raw object to read
     * @return this field's source iterator, positioned at the start of the object
     */
    private SourceIterator sourceIteratorFor(String obj) {
        VirtualAccess access = new VirtualAccess(obj.getBytes(Charset.defaultCharset()));

        try {
            if (this.sourceIterator == null) {
                this.sourceIterator = switch (this.referenceFormulation) {
                    case CSVRows -> new CSVSourceIterator(access);
                    case JSONPath -> new JSONSourceIterator(access, this.iterator);
                    case XMLPath -> new XMLSourceIterator(access, this.iterator == null ? "." : this.iterator);
                };
            } else {
                this.sourceIterator.reset(access);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return this.sourceIterator;
    }

    private List<SolutionMapping> processXML(Optional<String> input_obj) {
        //FIXME: also check for the case where input_obj is empty!
        String obj = input_obj.get();

        List<SolutionMapping> result = new ArrayList<>();

        try {
            SourceIterator xmlIterator = sourceIteratorFor(obj);
            while (xmlIterator.hasNext()) {
                XMLRecord record = (XMLRecord) xmlIterator.next();
                RecordValue value = record.get(".");
                if (value.isOk()) {
                    String sub = record.getItem().toString();
                    result.addAll(applySubfields(Optional.of(sub)));
                } else if (value.isError()) {
                    throw new RuntimeException(value.getMessage());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private List<SolutionMapping> processJSON(Optional<String> input_obj) {
        if (input_obj.isEmpty()) {

            return getSolutionMappingsForEmptyInputOrEmptyIterators(Optional.empty());
        }

        String obj = input_obj.get();
        SourceIterator jsonSourceIterator = sourceIteratorFor(obj);
        List<SolutionMapping> out = new ArrayList<>();
        while (jsonSourceIterator.hasNext()) {
            Record record = jsonSourceIterator.next();
            RecordValue recordValue = record.get("$");
            if (recordValue.isOk()) {
                String sub;
                Object readObject = recordValue.getValue();
                switch (readObject) {
                    case JSONArray array -> {
                        for (Object value : array) {
                            if (value instanceof LinkedHashMap<?, ?> map) {
                                sub = new JSONObject((Map<String, Object>) map).toJSONString();
                            } else {
                                sub = value.toString();
                            }
                            out.addAll(applySubfields(Optional.of(sub)));
                        }
                    }
                    case LinkedHashMap<?, ?> map -> {
                        sub = new JSONObject((Map<String, Object>) map).toJSONString();
                        out.addAll(applySubfields(Optional.of(sub)));
                    }
                    case TextNode textNode -> {
                        Collection<SolutionMapping> solutionMappings = applySubfields(Optional.of(textNode.asText()));
                        out.addAll(solutionMappings);
                    }
                    default -> {
                        Collection<SolutionMapping> solutionMappings = applySubfields(Optional.of(readObject.toString()));
                        out.addAll(solutionMappings);
                    }
                }
            } else if (recordValue.isError()) {
                throw new RuntimeException(recordValue.getMessage());
            }
        }

        if (out.isEmpty()) {
            return getSolutionMappingsForEmptyInputOrEmptyIterators(Optional.empty());
        }

        return out;
    }

    private List<SolutionMapping> getSolutionMappingsForEmptyInputOrEmptyIterators(Optional<String> input_obj) {
        Collection<SolutionMapping> subfieldsSolution = this.applySubfields(input_obj);
        return new ArrayList<>(subfieldsSolution);
    }

    private List<SolutionMapping> processCSV(Optional<String> input_obj) {
        //FIXME: also check for the case where input_obj is empty!
        String obj = input_obj.get();
        List<SolutionMapping> out = new ArrayList<>();

        try {
            SourceIterator iterator = sourceIteratorFor(obj);
            while (iterator.hasNext()) {
                CSVRecord r = (CSVRecord) iterator.next();

                String value;
                if (this.iterator != null) {
                    value = r.getData().get(this.iterator);
                } else {
                    // package the record
                    value = r.toCSVString();
                }
                out.addAll(applySubfields(Optional.of(value)));
            }

            return out;
        } catch (IOException e) {
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

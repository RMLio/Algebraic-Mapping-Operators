package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import com.opencsv.CSVWriter;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.jspecify.annotations.NonNull;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSVSourceOperator extends DataIOSourceOperator {

    private transient CSVSourceIterator iterator;

    public CSVSourceOperator(String operatorName, Access access) {
        this(operatorName, access, "default", List.of());
    }

    public CSVSourceOperator(String operatorName,
                             Access access,
                             String defaultFragment,
                             List<Field> fields) {
        super(operatorName, access, defaultFragment, fields);
        this.iterator = null;
    }

    @Override
    @NonNull
    public MappingTuple consumeSource() {
        MappingTuple tuple = new MappingTuple();
        List<SolutionMapping> out = new ArrayList<>();

        try {
            this.init();
            while (this.iterator.hasNext()) {
                CSVRecord r = (CSVRecord) this.iterator.next();

                String obj = processRecord(r);

                List<SolutionMapping> mappings = new ArrayList<>();
                for (Field f : this.fields) {
                    List<SolutionMapping> fieldMaps = f.apply(obj);
                    if (mappings.isEmpty()) {
                        mappings.addAll(fieldMaps);
                    } else if (!fieldMaps.isEmpty()) {
                        List<SolutionMapping> newMaps = new ArrayList<>();

                        for (SolutionMapping m : mappings) {
                            for (SolutionMapping fieldMap : fieldMaps) {
                                newMaps.add(m.union(fieldMap));
                            }
                        }

                        mappings = new ArrayList<>(newMaps);
                    }
                }

                // TODO: getIndex() instead of 0
                mappings.forEach(m -> m.put("#", new LiteralNode(this.iterator.getIndex(), XSDDatatype.XSDinteger)));

                out.addAll(mappings);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        tuple.setSolutionMaps("default", out);
        return tuple;
    }

    /**
     * Turn the record into a CSV value to be processed by the
     *
     * @param r
     * @return
     */
    private String processRecord(CSVRecord r) {
        Map<String, String> data = r.getData();
        String[] header = data.keySet().toArray(String[]::new);
        String[] items = new String[header.length];

        for (int i = 0; i < header.length; i++) {
            items[i] = data.get(header[i]);
        }

        StringWriter sw = new StringWriter();

        CSVWriter writer = new CSVWriter(sw);

        writer.writeAll(List.of(header, items));

        return sw.toString();
//        return header.stream().reduce((a,b) -> a + "," + b).get() + "\n" + item;
    }

    private SolutionMapping consumeRecord(CSVRecord r) {
        SolutionMapping map = new SolutionMapping();
        Map<String, String> data = r.getData();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            XSDDatatype datatype;
            String key = entry.getKey();

            String recordedDatatype = r.getDataType(key);
            if (recordedDatatype != null) {
                String datatypeExtract = recordedDatatype.substring(recordedDatatype.lastIndexOf('#') + 1);
                datatype = new XSDDatatype(datatypeExtract);
            } else {
                datatype = XSDDatatype.XSDstring;
            }


            String value = entry.getValue();
            if (value == null) {
                map.put(key, null);
            } else {
                map.put(key, new LiteralNode(value, datatype));
            }
        }

        return map;
    }

    @Override
    public boolean hasNext() {
        return this.isReady() && this.iterator != null && this.iterator.hasNext();
    }

    @Override
    @NonNull
    protected MappingTuple nextEffective() {
        MappingTuple tuple = new MappingTuple();
        CSVRecord r = (CSVRecord) this.iterator.next();
        SolutionMapping map = consumeRecord(r);
        tuple.addSolutionMap(this.defaultFragment, map);
        return tuple;
    }

    @Override
    public void init() throws SQLException, IOException, ParserConfigurationException, TransformerException {
        try {
            this.iterator = new CSVSourceIterator(this.access);
            this.setReady(true);
        } catch (Exception e) {
            this.setReady(false);
            throw e; // rethrow the exception to be explicit about it for the rest of code
        }
    }

}

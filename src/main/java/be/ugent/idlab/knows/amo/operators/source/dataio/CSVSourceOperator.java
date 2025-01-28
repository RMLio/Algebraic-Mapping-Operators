package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.jspecify.annotations.NonNull;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class CSVSourceOperator extends DataIOSourceOperator {

    private transient CSVSourceIterator iterator;

    public CSVSourceOperator(String operatorName, Access access) {
        this(operatorName, access, "default");
    }

    public CSVSourceOperator(String operatorName, Access access, String defaultFragment) {
        super(operatorName, access, defaultFragment);
        this.iterator = null;

    }

    @Override
    @NonNull
    public MappingTuple consumeSource() {
        MappingTuple tuple = new MappingTuple();
        try {
            this.init();
            while (this.iterator.hasNext()) {
                CSVRecord r = (CSVRecord) this.iterator.next();
                SolutionMapping map = consumeRecord(r);

                tuple.addSolutionMap(this.defaultFragment, map);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return tuple;
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

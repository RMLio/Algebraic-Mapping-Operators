package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import java.util.Map;

public class CSVSourceOperator extends DataIOSourceOperator {

    private CSVSourceIterator iterator;

    public CSVSourceOperator(String operatorName, Access access) {
        this(operatorName, access, "default");
    }

    public CSVSourceOperator(String operatorName, Access access, String defaultFragment) {
        super(operatorName, access, defaultFragment);
        this.iterator = null;

    }

    @Override
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
        for (String key : data.keySet()) {
            XSDDatatype datatype;
            String recordedDatatype = r.getDataType(key);
            if (recordedDatatype != null) {
                datatype = new XSDDatatype(recordedDatatype);
            } else {
                datatype = XSDDatatype.XSDstring;
            }

            Object value = data.get(key);
            if (data.get(key) == null) {
                value = "";
            }

            map.put(key, new LiteralNode(value.toString(), datatype));
        }

        return map;
    }

    @Override
    public boolean hasNext() {
        return this.isReady() && this.iterator != null && this.iterator.hasNext();
    }

    @Override
    protected MappingTuple nextEffective() {
        MappingTuple tuple = new MappingTuple();
        CSVRecord r = (CSVRecord) this.iterator.next();
        SolutionMapping map = consumeRecord(r);
        tuple.addSolutionMap(this.defaultFragment, map);
        return tuple;
    }

    @Override
    public void init() throws Exception {
        try {
            this.iterator = new CSVSourceIterator(this.access);
            this.setReady(true);
        } catch (Exception e) {
            this.setReady(false);
            throw new RuntimeException(e);
        }
    }

}

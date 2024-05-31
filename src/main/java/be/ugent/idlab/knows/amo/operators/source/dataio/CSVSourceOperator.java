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

    public CSVSourceOperator(Access access) {
        super(access);
    }

    @Override
    public MappingTuple consumeSource() {
        MappingTuple tuple = new MappingTuple();
        try (CSVSourceIterator iterator = new CSVSourceIterator(this.access)) {
            while (iterator.hasNext()) {
                CSVRecord r = (CSVRecord) iterator.next();
                SolutionMapping map = consumeRecord(r);

                tuple.addSolutionMap("f_default", map);
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

            map.put("?" + key, new LiteralNode(value.toString(), datatype));
        }

        return map;
    }
}

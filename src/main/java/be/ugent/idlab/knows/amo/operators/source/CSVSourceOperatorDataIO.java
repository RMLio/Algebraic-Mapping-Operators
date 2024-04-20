package be.ugent.idlab.knows.amo.operators.source;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.NodeFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CSVSourceOperatorDataIO extends SourceOperator {


    public CSVSourceOperatorDataIO(Access access) {
        super(access);
    }

    @Override
    public Collection<MappingTuple> getMappingTuples() {
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

        return List.of(tuple);
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

            map.put("?" + key, NodeFactory.createLiteralByValue(value, datatype));
        }

        return map;
    }
}

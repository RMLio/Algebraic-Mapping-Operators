package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.FieldBuilder;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import com.opencsv.CSVWriter;
import org.jspecify.annotations.NonNull;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.*;

public class CSVSourceOperator extends DataIOSourceOperator {

    private transient CSVSourceIterator iterator;

    /**
     * Constructs the CSV operator
     *
     * @param operatorName    name of the operator
     * @param access          access to consume
     * @param outputFragments The output fragments of the operator.
     * @param fields          fields to be present in generated solution mappings
     */
    public CSVSourceOperator(String operatorName,
                             Access access,
                             Set<String> outputFragments,
                             Collection<Field> fields,
                             Collection<String> nulls) {
        super(operatorName, access, outputFragments, fields, nulls);
        this.iterator = null;
    }

    /**
     * Turn the CSV record into string representation
     *
     * @param record    The CSV record to process
     * @return          A String representation (CSV serialization including headers) of the record.
     */
    private String processRecord(CSVRecord record) {
        Map<String, String> data = record.getData();
        String[] header = data.keySet().toArray(String[]::new);
        String[] items = new String[header.length];

        for (int i = 0; i < header.length; i++) {
            items[i] = data.get(header[i]);
        }

        // derive fields
        if (fields.isEmpty()) {
            Collection<Field> newFields = new ArrayList<>();
            for (String fieldName : header) {
                newFields.add(new FieldBuilder().withName(fieldName).CSV().withReference(fieldName).build());
            }
            fields = newFields;
        }

        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);
        writer.writeAll(List.of(header, items));

        return sw.toString();
    }

    @Override
    public boolean hasNext() {
        if (!this.isReady()) {
            try {
                this.init();
            } catch (SQLException | IOException | ParserConfigurationException | TransformerException e) {
                throw new RuntimeException(e);
            }
        }

        return this.isReady() && this.iterator != null && this.iterator.hasNext();
    }

    @Override
    @NonNull
    protected MappingTuple nextEffective() {
        MappingTuple tuple = new MappingTuple();
        CSVRecord record = (CSVRecord) this.iterator.next();

        String obj = processRecord(record);

        List<SolutionMapping> mappings = applySubfields(obj, this.iterator.getIndex());

        for (String outputFragment : getOutputFragments()) {
            tuple.setSolutionMaps(outputFragment, mappings);
        }
        return tuple;
    }

    @Override
    public void init() throws SQLException, IOException, ParserConfigurationException, TransformerException {
        if (this.isReady()) {
            return;
        }

        try {
            this.iterator = new CSVSourceIterator(this.access, this.nulls);
            this.setReady(true);
        } catch (Exception e) {
            this.setReady(false);
            throw e; // rethrow the exception to be explicit about it for the rest of code
        }
    }

}

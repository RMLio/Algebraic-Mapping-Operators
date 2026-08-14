package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.FieldBuilder;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.CSVWSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import com.opencsv.CSVWriter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.*;

public class CSVSourceOperator extends DataIOSourceOperator {

    private transient CSVWSourceIterator iterator;

    /**
     * How the rows are written, when the source says so itself. A CSV read with the
     * defaults leaves this null.
     */
    private final @Nullable CSVWConfiguration configuration;

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
        this(operatorName, access, outputFragments, fields, nulls, null);
    }

    /**
     * Constructs the CSV operator for a source that says how its rows are written: a CSV on
     * the Web table, whose dialect gives the delimiter, the quote character, the encoding
     * and the rest.
     *
     * @param operatorName    name of the operator
     * @param access          access to consume
     * @param outputFragments The output fragments of the operator.
     * @param fields          fields to be present in generated solution mappings
     * @param nulls           The values considered a {@code null} value.
     * @param configuration   the dialect the rows are written in, null to read them the
     *                        way a plain CSV is read
     */
    public CSVSourceOperator(String operatorName,
                             Access access,
                             Set<String> outputFragments,
                             Collection<Field> fields,
                             Collection<String> nulls,
                             @Nullable CSVWConfiguration configuration) {
        super(operatorName, access, outputFragments, fields, nulls);
        this.iterator = null;
        this.configuration = configuration;
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

        List<SolutionMapping> mappings = applyFields(obj, this.iterator.getIndex());

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
            // a plain CSV is a CSVW read with the defaults, which is what the iterator
            // without a dialect does; only a source saying otherwise needs its own
            this.iterator = this.configuration == null
                    ? new CSVSourceIterator(this.access, this.nulls)
                    : new CSVWSourceIterator(this.access, this.configuration);
            this.setReady(true);
        } catch (Exception e) {
            this.setReady(false);
            throw e; // rethrow the exception to be explicit about it for the rest of code
        }
    }

}

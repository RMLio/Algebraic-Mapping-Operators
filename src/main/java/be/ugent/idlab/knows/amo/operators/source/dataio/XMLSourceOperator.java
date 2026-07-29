package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.ExpressionField;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.IteratorField;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.dataio.record.RecordValue;
import be.ugent.idlab.knows.dataio.record.XMLRecord;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class XMLSourceOperator extends DataIOSourceOperator {
    private final String rootIterator;
    private transient XMLSourceIterator sourceIterator;
    private Map<String, String> namespaces; 

    public XMLSourceOperator(String operatorName, Access access, Set<String> outputFragments,
                             String rootIterator,
                             Collection<Field> fields,
                             Collection<String> nulls, Map<String, String> namespaces) {
        this(operatorName, access, outputFragments, rootIterator, fields, nulls); 
        this.namespaces = namespaces; 
    }
    public XMLSourceOperator(String operatorName, Access access, Set<String> outputFragments,
                             String rootIterator,
                             Collection<Field> fields,
                             Collection<String> nulls) {
        super(operatorName, access, outputFragments, fields, nulls);
        this.namespaces = new HashMap<>(); 
        this.rootIterator = rootIterator;
        this.sourceIterator = null;

        prepareFields(fields);
        this.nulls = nulls;
    }

    private void prepareFields(Collection<Field> fields) {
        String[] iteratorSplit = this.rootIterator.split("/");
        String lastIteratorPart = iteratorSplit[iteratorSplit.length - 1];

        this.fields = new HashSet<>();

        for (Field f : fields) {
            Field newField = switch (f) {
                // the field reads from within the root iterator, so whatever its
                // expression references is resolved relative to it
                case ExpressionField ef -> ef.relativeTo("./%s".formatted(lastIteratorPart));
                case IteratorField it -> Field.builder()
                        .withReferenceFormulation(it.getReferenceFormulation())
                        .withIterator("./%s/%s".formatted(lastIteratorPart, it.getIterator()))
                        .withName(it.name())
                        .withSubfields(it.getSubfields())
                        .build();

                default -> throw new IllegalStateException("Unknown field: " + f);
            };

            this.fields.add(newField);
        }
    }


    @Override
    @NonNull
    protected MappingTuple nextEffective() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }

        MappingTuple out = new MappingTuple();

        XMLRecord record = (XMLRecord) this.sourceIterator.next();
        RecordValue value = record.get("/");
        if (value.isOk()) {
            List<SolutionMapping> sms = applyFields(record.getItem().toString(), record.getIndex());
            for (String outputFragment : getOutputFragments()) {
                out.setSolutionMaps(outputFragment, sms);
            }
        } else if (value.isError()) {
            throw new NoSuchElementException(value.getMessage());
        }
        // else value is empty or not found: continue

        return out;
    }

    @Override
    public boolean hasNext() {
        if (!this.isReady()) {
            this.init();
        }

        return this.isReady() && this.sourceIterator != null && this.sourceIterator.hasNext();
    }

    @Override
    public void init() {
        if (this.isReady()) {
            return;
        }

        try {
            this.sourceIterator = new XMLSourceIterator(this.access, this.rootIterator, this.namespaces);
            this.setReady(true);
        } catch (Exception e) {
            this.setReady(false);
            throw new RuntimeException(e);

        }
    }
}

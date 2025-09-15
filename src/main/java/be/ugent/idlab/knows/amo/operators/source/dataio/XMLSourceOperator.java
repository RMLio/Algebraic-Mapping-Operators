package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.*;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.dataio.record.XMLRecord;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class XMLSourceOperator extends DataIOSourceOperator {
    private final String rootIterator;
    private transient XMLSourceIterator sourceIterator;

    public XMLSourceOperator(String operatorName, Access access, Set<String> outputFragments,
                             String rootIterator,
                             Collection<Field> fields,
                             Collection<String> nulls) {
        super(operatorName, access, outputFragments, fields, nulls);
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
                case ConstantField cf -> cf;
                case ReferenceField rf -> Field.builder()
                        .withReferenceFormulation(rf.getReferenceFormulation())
                        .withReference("./%s/%s".formatted(lastIteratorPart, rf.getReference()))
                        .withName(rf.name())
                        .withSubfields(rf.getSubfields())
                        .build();
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

        XMLRecord r = (XMLRecord) this.sourceIterator.next();
        List<SolutionMapping> sms = applySubfields(r.getItem().toString(), r.getIndex());

        MappingTuple out = new MappingTuple();
        for (String outputFragment : getOutputFragments()) {
            out.setSolutionMaps(outputFragment, sms);
        }
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
            this.sourceIterator = new XMLSourceIterator(this.access, this.rootIterator);
            this.setReady(true);
        } catch (Exception e) {
            this.setReady(false);
            throw new RuntimeException(e);

        }
    }
}

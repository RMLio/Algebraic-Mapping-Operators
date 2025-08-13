package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.ConstantField;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.IteratorField;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.ReferenceField;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.dataio.record.XMLRecord;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class XMLSourceOperator extends DataIOSourceOperator {
    private final String rootIterator;
    private transient XMLSourceIterator sourceIterator;

    public XMLSourceOperator(String operatorName, Access access, String defaultFragment,
                             String rootIterator,
                             List<Field> fields,
                             List<String> nulls) {
        super(operatorName, access, defaultFragment, fields, nulls);
        this.rootIterator = rootIterator;
        this.sourceIterator = null;

        // prepend the root iterator to all fields
//        this.fields = prepareFields(fields, this.rootIterator);
        this.fields = fields;
        this.nulls = nulls;
    }

    private List<Field> prepareFields(List<Field> fields, String rootIterator) {
        List<Field> out = new ArrayList<>();

        boolean containsParentReference = false;
        for (Field f : fields) {
            containsParentReference = switch (f) {
                case IteratorField it -> it.getIterator().startsWith("..");
                case ReferenceField rf -> rf.getReference().startsWith("..");
                case ConstantField cf -> false;
                default -> throw new IllegalStateException("Unexpected field type: " + f);
            };

            if (containsParentReference) {
                break;
            }
        }

        if (containsParentReference) {
            // adjust all fields by one element
            String[] rootIteratorParts = rootIterator.split("/");

        }

        return out;




//        List<Field> out = new ArrayList<>();
//        for (Field f : fields) {
//            switch (f) {
//                case IteratorField it -> {
//                    String currentIterator = rootIterator + (it.getIterator() == null ? "" : "/" + it.getIterator());
//                    List<Field> subfields = prepareFields((List<Field>) f.getSubfields(), currentIterator);
//                    out.add(new IteratorField(it.name(), subfields, it.getReferenceFormulation(), currentIterator));
//                }
//                case ReferenceField ref -> {
//                    String currentIterator = rootIterator + "/" + ref.getReference();
//                    List<Field> subfields = prepareFields((List<Field>) f.getSubfields(), currentIterator);
//
//                    out.add(new ReferenceField(ref.name(), subfields, ref.getReferenceFormulation(), currentIterator));
//                }
//                case ConstantField ignored -> out.add(f);
//                case null, default -> throw new IllegalStateException("Unknown / unsupported field type");
//            }
//        }
//
//        return out;
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
        out.setSolutionMaps(this.defaultFragment, sms);

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

package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.NullNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.dataio.record.Record;
import be.ugent.idlab.knows.dataio.record.RecordValue;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class XMLSourceOperator extends DataIOSourceOperator {
    private final Collection<String> rootVariables;
    private final String rootIterator;
    private final Collection<String> subIterators;
    private final Collection<Pair<String, String>> aliases;
    private transient XMLSourceIterator sourceIterator;
    private final Map<String, RDFNode> defaultValues;

    public XMLSourceOperator(String operatorName, Access access, String defaultOperator,
                             Collection<String> rootVariables, String rootIterator,
                             Collection<String> subIterators,
                             Collection<Pair<String, String>> aliases,
                             Map<String, RDFNode> defaultValues) {
        super(operatorName, access, defaultOperator);
        this.rootVariables = rootVariables;
        this.rootIterator = rootIterator;
        this.subIterators = subIterators;
        this.aliases = aliases;
        this.defaultValues = defaultValues;
        this.sourceIterator = null;
    }

    @Override
    @NonNull
    public MappingTuple consumeSource() {
        MappingTuple mappingTuple = new MappingTuple();
        try {
            this.init();
            XMLSourceIterator xmlSourceIterator = this.sourceIterator;
            while (xmlSourceIterator.hasNext()) {
                Record r = xmlSourceIterator.next();
                SolutionMapping m = new SolutionMapping();
                // consume variables to be fetched from the root iterator
                consumeRecord(r, this.rootVariables, m);
                // consume any and all subiterators with respect to the root iterator
                consumeRecord(r, this.subIterators, m);

                mappingTuple.addSolutionMap("default", m);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mappingTuple;
    }

    private void consumeRecord(Record r, Collection<String> iterators, SolutionMapping mapping) {
        mapping.putAll(this.defaultValues);
        for (String it : iterators) {
            RecordValue recordValue = r.get(it);

            if (recordValue.isOk()) {
                // when RecordValue is ok, the result of the iterator is a list
                //noinspection unchecked
                List<String> value = (List<String>) recordValue.getValue();
                if (value.size() == 1) {
                    mapping.put(it, new LiteralNode(value.getFirst(), XSDDatatype.XSDstring));
                } else {
                    mapping.put(it, new LiteralNode(recordValue.getValue().toString(), XSDDatatype.XSDstring));
                }
            } else {
                mapping.put(it, new NullNode());
            }
        }


        for (Pair<String, String> pair : aliases) {
            if (mapping.containsKey(pair.first())) {
                mapping.put(pair.second(), mapping.get(pair.first()));
                mapping.remove(pair.first());
            }
        }
    }

    @Override
    @NonNull
    protected MappingTuple nextEffective() {
        MappingTuple tuple = new MappingTuple();
        Record r = this.sourceIterator.next();
        SolutionMapping m = new SolutionMapping();
        // consume variables to be fetched from the root iterator
        consumeRecord(r, this.rootVariables, m);
        // consume any and all subiterators with respect to the root iterator
        consumeRecord(r, this.subIterators, m);

        tuple.addSolutionMap(this.defaultFragment, m);

        return tuple;

    }

    @Override
    public boolean hasNext() {
        return this.isReady() && this.sourceIterator != null && this.sourceIterator.hasNext();
    }

    @Override
    public void init() {
        try {
            this.sourceIterator = new XMLSourceIterator(this.access, this.rootIterator);
            this.setReady(true);
        } catch (Exception e) {
            this.setReady(false);
            throw new RuntimeException(e);

        }
    }
}

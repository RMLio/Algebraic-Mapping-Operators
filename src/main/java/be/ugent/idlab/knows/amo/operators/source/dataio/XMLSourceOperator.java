package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.dataio.record.Record;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import java.util.Collection;
import java.util.List;

public class XMLSourceOperator extends DataIOSourceOperator {
    private final Collection<String> rootVariables;
    private final String rootIterator;
    private final Collection<String> subIterators;

    public XMLSourceOperator(String operatorName, Access access, Collection<String> rootVariables, String rootIterator, Collection<String> subIterators) {
        this(operatorName, access, "default", rootVariables, rootIterator, subIterators);
    }

    public XMLSourceOperator(String operatorName, Access access, String defaultOperator, Collection<String> rootVariables, String rootIterator, Collection<String> subIterators) {
        super(operatorName, access, defaultOperator);
        this.rootVariables = rootVariables;
        this.rootIterator = rootIterator;
        this.subIterators = subIterators;
    }

    @Override
    public MappingTuple consumeSource() {
        MappingTuple mappingTuple = new MappingTuple();
        try (XMLSourceIterator xmlSourceIterator = new XMLSourceIterator(this.access, this.rootIterator)) {
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
        for (String it : iterators) {
            List<Object> values = r.get(it);
            String value;
            if (!values.isEmpty()) {
                value = values.getFirst().toString();
            } else {
                value = "";
            }
            mapping.put(it, new LiteralNode(value, XSDDatatype.XSDstring));
        }
    }
}

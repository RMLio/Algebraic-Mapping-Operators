package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.operators.source.SourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.jspecify.annotations.NullMarked;

import java.util.*;

/**
 * Operator responsible for reading input into the plan.
 */
@NullMarked
public abstract class DataIOSourceOperator extends SourceOperator {

    protected Access access;
    protected Collection<Field> fields;
    protected Collection<String> nulls;

    /**
     * Instantiates a new DataIOSourceOperator.
     *
     * @param operatorName      The name (identifier) of the operator.
     * @param access            The data source this operator gets its data from.
     * @param outputFragments   The output fragments of the operator.
     * @param fields            The relevant data fields (e.g. CSV headers or JSON keys)
     * @param nulls             The values considered a {@code null} value.
     */
    protected DataIOSourceOperator(String operatorName, Access access, Set<String> outputFragments, Collection<Field> fields, Collection<String> nulls) {
        super(operatorName, outputFragments);
        this.access = access;
        this.fields = fields;
        this.nulls = new HashSet<>(nulls);
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    protected List<SolutionMapping> applyFields(String object, int index) {
        List<SolutionMapping> mappings = new ArrayList<>();

        for (Field f : this.fields) {
            List<SolutionMapping> fieldMaps = f.apply(Optional.of(object));
            if (mappings.isEmpty()) {
                mappings.addAll(fieldMaps);
            } else if (!fieldMaps.isEmpty()) {
                List<SolutionMapping> newMaps = new ArrayList<>();

                for (SolutionMapping m : mappings) {
                    for (SolutionMapping fieldMap : fieldMaps) {
                        newMaps.add(m.union(fieldMap));
                    }
                }

                mappings = newMaps;
            }
        }

        mappings.forEach(m -> m.put("#", new LiteralNode(index, XSDDatatype.XSDinteger)));

        return mappings;
    }

    public MappingTuple consumeSource() {
        MappingTuple tuple = new MappingTuple();
            while (this.hasNext()) {
                MappingTuple nextTuple = this.nextEffective();
                tuple = tuple.union(nextTuple);
            }
        return tuple;
    }
}

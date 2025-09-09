package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.operators.source.SourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * Operator responsible for reading input into the plan.
 */
@NullMarked
public abstract class DataIOSourceOperator extends SourceOperator {

    protected Access access;
    protected String defaultFragment;
    protected List<Field> fields;
    protected List<String> nulls;

    public DataIOSourceOperator(String operatorName, Access access, String defaultFragment, List<Field> fields, List<String> nulls) {
        super(operatorName);
        this.access = access;
        this.defaultFragment = defaultFragment;
        this.fields = fields;
        this.nulls = new ArrayList<>(nulls);
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public String getDefaultFragment() {
        return this.defaultFragment;
    }

    protected List<SolutionMapping> applySubfields(String object, int index) {
        List<SolutionMapping> mappings = new ArrayList<>();

        for (Field f : this.fields) {
            List<SolutionMapping> fieldMaps = f.apply(object);
            if (mappings.isEmpty()) {
                mappings.addAll(fieldMaps);
            } else {
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

package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.NullNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.functions.ExtendFunction;
import be.ugent.idlab.knows.dataio.access.VirtualAccess;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.SourceIterator;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import javax.swing.text.html.Option;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class Field implements Serializable {
    protected final String name;
    protected final Collection<Field> subfields;
    protected final ReferenceFormulation referenceFormulation;

    public Field(String name, Collection<Field> subfields, ReferenceFormulation referenceFormulation) {
        this.name = name;
        this.subfields = subfields;
        this.referenceFormulation = referenceFormulation;
    }

    protected static RDFNode getLiteralNode(Object value) {
        if (value == null) {
            return new NullNode();
        }

        if (value instanceof Number) {
            return new LiteralNode(value, XSDDatatype.XSDinteger);
        }

        return new LiteralNode(value);
    }

    public static FieldBuilder builder() {
        return new FieldBuilder();
    }

    public String name() {
        return this.name;
    }

    public abstract List<SolutionMapping> apply(Optional<String> obj);

    protected SourceIterator getSourceIterator(String obj, ReferenceFormulation ref) throws Exception {
        return this.getSourceIterator(obj, "", ref);
    }

    protected SourceIterator getSourceIterator(String obj, String iterator, ReferenceFormulation ref) throws Exception {
        VirtualAccess acc = new VirtualAccess(obj.getBytes(Charset.defaultCharset()));

        return switch (ref) {
            case CSVRows -> new CSVSourceIterator(acc);
            case JSONPath -> new JSONSourceIterator(acc, iterator);
            case XMLPath -> new XMLSourceIterator(acc, iterator);
        };
    }

    public ReferenceFormulation getReferenceFormulation() {
        return this.referenceFormulation;
    }

    protected Collection<SolutionMapping> applySubfields(Optional<String> value) {
        List<SolutionMapping> out = new ArrayList<>();
        for (Field field : this.subfields) {
            List<SolutionMapping> sub = field.apply(value);

            if (out.isEmpty()) {
                out.addAll(sub);
            } else {
                out = new ArrayList<>(
                        out.stream()
                                .flatMap(sm -> sub.stream().map(sm::union))
                                .toList()
                );
            }
        }

        return out;
    }

    public Collection<Field> getSubfields() {
        return subfields;
    }

    /**
     * Reads the raw record's columns into a solution mapping and evaluates the given
     * expression against it, binding the result under this field's name. Used by fields
     * whose value is computed by a function on the data (e.g. {@code toUpperCase(name)}):
     * the function is applied first, and its result becomes the field's value.
     *
     * @param obj        the raw record
     * @param expression the function to apply to the record's columns
     * @return one solution mapping per record row, binding the computed value to this field's name
     */
    protected List<SolutionMapping> applyExpression(Optional<String> obj, ExtendFunction expression) {
        if (obj.isEmpty()) {
            return List.of();
        }
        if (this.referenceFormulation != ReferenceFormulation.CSVRows) {
            throw new UnsupportedOperationException(
                    "Computed fields (a function applied to the data) are only supported for CSV sources for now; field '"
                            + this.name + "'.");
        }

        VirtualAccess access = new VirtualAccess(obj.get().getBytes(Charset.defaultCharset()));
        List<SolutionMapping> out = new ArrayList<>();
        try (CSVSourceIterator iterator = new CSVSourceIterator(access)) {
            int index = 0;
            while (iterator.hasNext()) {
                CSVRecord record = (CSVRecord) iterator.next();
                SolutionMapping base = new SolutionMapping();
                for (Map.Entry<String, String> entry : record.getData().entrySet()) {
                    base.put(entry.getKey(), getLiteralNode(entry.getValue()));
                }
                SolutionMapping sm = new SolutionMapping();
                sm.put(this.name, getLiteralNode(expression.apply(base)));
                sm.put(this.name + ".#", getLiteralNode(index));
                out.add(sm);
                index++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return out;
    }
}


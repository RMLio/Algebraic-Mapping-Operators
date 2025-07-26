package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.NullNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.dataio.access.VirtualAccess;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.SourceIterator;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public abstract List<SolutionMapping> apply(String obj);

    protected SourceIterator getSourceIterator(String obj, ReferenceFormulation ref) throws Exception {
        return this.getSourceIterator(obj, "", ref);
    }

    protected SourceIterator getSourceIterator(String obj, String iterator, ReferenceFormulation ref) throws Exception {
        VirtualAccess acc = new VirtualAccess(obj.getBytes(Charset.defaultCharset()));

        return switch (ref) {
            case CSVRows -> new CSVSourceIterator(acc);
            case JSONPath -> new JSONSourceIterator(acc, iterator);
            case XPath -> new XMLSourceIterator(acc, iterator);
        };
    }

    public ReferenceFormulation getReferenceFormulation() {
        return this.referenceFormulation;
    }

    protected Collection<SolutionMapping> applySubfields(String value) {
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

}


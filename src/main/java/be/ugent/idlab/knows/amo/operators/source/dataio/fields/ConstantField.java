package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.functions.ExtendFunction;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConstantField extends Field {
    private final RDFNode constant;
    private final ExtendFunction expression;

    public ConstantField(String name, Collection<Field> subfields, ReferenceFormulation referenceFormulation, RDFNode constant) {
        this(name, subfields, referenceFormulation, constant, null);
    }

    public ConstantField(String name, Collection<Field> subfields, ReferenceFormulation referenceFormulation, RDFNode constant, ExtendFunction expression) {
        super(name, subfields, referenceFormulation);
        this.constant = constant;
        this.expression = expression;
    }

    public RDFNode getConstant() {
        return constant;
    }

    @Override
    public List<SolutionMapping> apply(Optional<String> obj) {
        // A computed field: apply the function to the record's columns and bind the result.
        if (this.expression != null) {
            return applyExpression(obj, this.expression);
        }

        return List.of(new SolutionMapping(Map.of(
                this.name, this.constant,
                this.name + ".#", new LiteralNode(0, XSDDatatype.XSDinteger)
        )));
    }
}

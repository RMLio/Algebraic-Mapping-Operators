package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConstantField extends Field {
    private final RDFNode constant;

    public ConstantField(String name, Collection<Field> subfields, ReferenceFormulation referenceFormulation, RDFNode constant) {
        super(name, subfields, referenceFormulation);
        this.constant = constant;
    }

    public RDFNode getConstant() {
        return constant;
    }

    @Override
    public List<SolutionMapping> apply(Optional<String> obj) {
        return List.of(new SolutionMapping(Map.of(
                this.name, this.constant,
                this.name + ".#", new LiteralNode(0, XSDDatatype.XSDinteger)
        )));
    }
}

package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import java.util.Map;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;

public class TemplateSerializer extends UnaryOperator {

    private String serializedVariable;
    private String template;

    public TemplateSerializer(String operatorName, String fragment, String template) {
        this(operatorName, fragment, template, "serialized_output");
    }

    public TemplateSerializer(String operatorName, String fragment, String template, String serializedVariable) {
        super(operatorName, fragment);

        this.serializedVariable = serializedVariable;
        this.template = template;

    }

    @Override
    SolutionMapping apply(SolutionMapping mapping) {

        SolutionMapping result = new SolutionMapping();
        String serializedString = this.template;
        for (Map.Entry<String, RDFNode> entry : mapping.entrySet()) {
            serializedString.replaceAll("\\" + entry.getKey(), entry.getValue().toString());
        }

        result.put(this.serializedVariable, new LiteralNode(serializedString));
        return result;
    }

    public String getSerializedVariable() {
        return serializedVariable;
    }
}

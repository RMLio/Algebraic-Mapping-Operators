package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simpleflatmapper.tuple.Tuple2;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;

public class TemplateSerializer extends UnaryOperator {

    private String serializedVariable;
    private List<Tuple2<Set<String>, String>> variablesTemplatePairs;
    private Pattern variablePattern;

    public TemplateSerializer(String operatorName, String fragment, String templateString) {
        this(operatorName, fragment, templateString, "serialized_output");
    }

    public TemplateSerializer(String operatorName, String fragment, String templateString, String serializedVariable) {
        super(operatorName, fragment);

        this.serializedVariable = serializedVariable;
        this.variablePattern = Pattern.compile("\\?[a-zA-Z_0-9]+[^@^^]");

        this.variablesTemplatePairs = this.extractTemplateVariables(templateString);

    }

    private List<Tuple2<Set<String>, String>> extractTemplateVariables(String template) {

        List<Tuple2<Set<String>, String>> result = new ArrayList<>();
        String[] newlinedTemplates = template.split("\n");

        for (String line : newlinedTemplates) {
            Matcher matcher = this.variablePattern.matcher(line);
            Set<String> variables = new HashSet<>();
            while (matcher.find()) {
                variables.add(matcher.group().trim());
            }
            result.add(new Tuple2<Set<String>, String>(variables, line));
        }

        return result;
    }

    @Override
    SolutionMapping apply(SolutionMapping mapping) {

        SolutionMapping result = new SolutionMapping();
        List<String> serializedStringList = new ArrayList<>();
        for (Tuple2<Set<String>, String> tuple : this.variablesTemplatePairs) {

            String template = tuple.getElement1();
            Set<String> variables = tuple.getElement0();

            for (String variable : variables) {
                template = template.replaceAll("\\" + variable, mapping.get(variable).toString());
            }

            serializedStringList.add(template);
        }

        String serializedString = String.join("\n", serializedStringList);

        result.put(this.serializedVariable, new LiteralNode(serializedString));
        return result;
    }

    public String getSerializedVariable() {
        return serializedVariable;
    }

    public List<Tuple2<Set<String>, String>> getVariablesTemplatePairs() {
        return variablesTemplatePairs;
    }

}

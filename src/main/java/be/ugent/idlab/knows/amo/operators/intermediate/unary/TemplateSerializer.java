package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;

import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;

public class TemplateSerializer extends UnaryOperator {

    private String serializedVariable;
    private List<Pair<List<String>, String>> variablesTemplatePairs;
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

    private List<Pair<List<String>, String>> extractTemplateVariables(String template) {

        List<Pair<List<String>, String>> result = new ArrayList<>();
        String[] newlinedTemplates = template.split("\n");

        for (String line : newlinedTemplates) {
            Matcher matcher = this.variablePattern.matcher(line);
            List<String> variables = new ArrayList<>();
            while (matcher.find()) {
                variables.add(matcher.group().trim());
            }
            variables.sort((o1, o2) -> o2.length() - o1.length());
            result.add(new Pair<>(variables, line));
        }

        return result;
    }

    @Override
    @Nullable
    SolutionMapping apply(@Nullable SolutionMapping mapping) {
        if (mapping == null) {
            return null;
        }

        SolutionMapping result = new SolutionMapping();
        List<String> serializedStringList = new ArrayList<>();
        for (Pair<List<String>, String> tuple : this.variablesTemplatePairs) {

            List<String> variables = tuple.first();
            String template = tuple.second();

            boolean nullFound = false;
            for (String variable : variables) {
                RDFNode solutionValue = mapping.get(variable);
                if (solutionValue != null) {
                    template = template.replaceAll("\\" + variable, solutionValue.getStringRepr());
                } else {
                    nullFound = true;
                    break;

                }
            }

            if (!nullFound) {
                serializedStringList.add(template);
            }
        }

        String serializedString = String.join("\n", serializedStringList);

        result.put(this.serializedVariable, new LiteralNode(serializedString));
        return result;
    }

    public String getSerializedVariable() {
        return serializedVariable;
    }

    public List<Pair<List<String>, String>> getVariablesTemplatePairs() {
        return variablesTemplatePairs;
    }

}

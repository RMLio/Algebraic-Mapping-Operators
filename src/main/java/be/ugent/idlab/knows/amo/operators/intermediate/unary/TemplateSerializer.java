package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.NullNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateSerializer extends UnaryOperator {

    private final String serializedVariable;
    private final List<Pair<List<String>, String>> variablesTemplatePairs;
    private final Pattern variablePattern;

    /**
     * Instantiates a new TemplateSerializer. After applying this operator the resulting solution mapping will use
     * the key {@code serialized_output} to store the serialized string.
     *
     * @param operatorName          A name (identifier) for the operator.
     * @param inputFragments        The input fragments of the operator.
     * @param outputFragments       The output fragments of the operator.
     * @param templateString        The template to fill in the results of the mappings. E.g. {@code ?sm ?pm ?om@en.}
     */
    public TemplateSerializer(String operatorName, Set<String> inputFragments, Set<String> outputFragments, String templateString) {
        this(operatorName, inputFragments, outputFragments, templateString, "serialized_output");
    }

    /**
     * Instantiates a new TemplateSerializer.
     *
     * @param operatorName          A name (identifier) for the operator.
     * @param inputFragments        The input fragments of the operator.
     * @param outputFragments       The output fragments of the operator.
     * @param templateString        The template to fill in the results of the mappings. E.g. {@code ?sm ?pm ?om@en.}
     * @param serializedVariable    The variable name used in the resulting solution mapping to store the serialized string when applying this operator.
     */
    public TemplateSerializer(String operatorName, Set<String> inputFragments, Set<String> outputFragments, String templateString, String serializedVariable) {
        super(operatorName, inputFragments, outputFragments);

        this.serializedVariable = serializedVariable;
        // a variable name is matched as far as it goes, so that ?om2 is one variable and
        // not ?om followed by a 2
        this.variablePattern = Pattern.compile("\\?[a-zA-Z_0-9]+");

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

            String template = tuple.second();

            // One pass over the template, looking each variable up where it occurs. Filling
            // the variables in one at a time instead would substitute into the values
            // already filled in: a value containing "?pm" would have the value of ?pm put
            // inside it by a later round.
            Matcher matcher = this.variablePattern.matcher(template);
            StringBuilder filledIn = new StringBuilder();

            boolean nullFound = false;
            while (matcher.find()) {
                String variable = matcher.group();

                RDFNode solutionValue = mapping.get(variable);
                if (solutionValue == null || solutionValue.isNull()) {
                    // check if the variable maybe needs to be prepended by ?
                    solutionValue = mapping.get(variable.substring(1));
                }

                if (solutionValue == null || solutionValue.isNull()) {
                    nullFound = true;
                    break;
                }

                // the value is data, not a replacement pattern: a '$' in it is a dollar and
                // a '\' is a backslash
                matcher.appendReplacement(filledIn, Matcher.quoteReplacement(solutionValue.toString()));
            }

            if (!nullFound) {
                matcher.appendTail(filledIn);
                serializedStringList.add(filledIn.toString());
            }
        }

        if (serializedStringList.isEmpty()) {
            result.put(serializedVariable, new NullNode());
        } else {
            String serializedString = String.join("\n", serializedStringList);
            result.put(this.serializedVariable, new LiteralNode(serializedString));
        }
        return result;
    }

    public String getSerializedVariable() {
        return serializedVariable;
    }

    public List<Pair<List<String>, String>> getVariablesTemplatePairs() {
        return variablesTemplatePairs;
    }

}

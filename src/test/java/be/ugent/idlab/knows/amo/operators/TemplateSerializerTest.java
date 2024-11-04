package be.ugent.idlab.knows.amo.operators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.junit.jupiter.api.Test;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.IRINode;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.operators.intermediate.unary.TemplateSerializer;

public class TemplateSerializerTest {

    @Test
    public void variableExtractionTest() {
        String template = """
                ?sm ?pm_1 ?om@en.
                ?sm <http://example.com/name/> ?om2^^<http://example.com/string>.
                """;
        TemplateSerializer serializer = new TemplateSerializer("Serializer", "default", template);

        List<Pair<List<String>, String>> pairs = serializer.getVariablesTemplatePairs()
                .stream()
                .map((tuple) -> new Pair<List<String>, String>(
                        tuple.first().stream().sorted().collect(Collectors.toList()),
                        tuple.second()))
                .collect(Collectors.toList());
        List<Pair<List<String>, String>> expected = new ArrayList<>();
        expected.add(new Pair<List<String>, String>(List.of("?om", "?pm_1", "?sm"), "?sm ?pm_1 ?om@en."));
        expected.add(new Pair<List<String>, String>(List.of("?om2", "?sm"),
                "?sm <http://example.com/name/> ?om2^^<http://example.com/string>."));

        assertEquals(expected, pairs);

    }

    @Test
    public void simpleTemplateSerialization() {

        MappingTuple mappingTuple = new MappingTuple();
        SolutionMapping solutionMapping = new SolutionMapping();
        solutionMapping.put("?sm", new IRINode("http://example.com/1"));
        solutionMapping.put("?pm", new IRINode("http://example.com/name"));
        solutionMapping.put("?om", new LiteralNode("Min Oo"));

        mappingTuple.addSolutionMap("default", solutionMapping);

        TemplateSerializer serializer = new TemplateSerializer("Serializer", "default", "?sm ?pm ?om@en.");

        MappingTuple serializedTuple = serializer.apply(mappingTuple);

        MappingTuple expected = new MappingTuple();;
        SolutionMapping expectedSolutionMapping = new SolutionMapping();
        expectedSolutionMapping.put("serialized_output",
                new LiteralNode("<http://example.com/1> <http://example.com/name> \"Min Oo\"@en."));
        expected.addSolutionMap("default", expectedSolutionMapping);
        assertEquals(expected, serializedTuple);

    }

    @Test
    public void datatypeTemplateSerialization() {


        MappingTuple mappingTuple = new MappingTuple();
        SolutionMapping solutionMapping = new SolutionMapping();
        solutionMapping.put("?sm", new IRINode("http://example.com/10/Venus"));
        solutionMapping.put("?pm", new IRINode("http://example.com/id"));
        solutionMapping.put("?om", new LiteralNode(10, new XSDDatatype("integer")));


        mappingTuple.addSolutionMap("default", solutionMapping);

        TemplateSerializer serializer = new TemplateSerializer("Serializer", "default", "?sm ?pm ?om .");

        MappingTuple serializedTuple = serializer.apply(mappingTuple);

        MappingTuple expected = new MappingTuple();
        SolutionMapping expectedSolutionMapping = new SolutionMapping();
        expectedSolutionMapping.put("serialized_output",
                new LiteralNode("<http://example.com/10/Venus> <http://example.com/id> \"10\"^^<http://www.w3.org/2001/XMLSchema#integer> ."));
        expected.addSolutionMap("default", expectedSolutionMapping);
        assertEquals(expected, serializedTuple);

    }
}

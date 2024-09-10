package be.ugent.idlab.knows.amo.operators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.simpleflatmapper.tuple.Tuple2;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
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

        List<Tuple2<List<String>, String>> pairs = serializer.getVariablesTemplatePairs()
                .stream()
                .map((tuple) -> new Tuple2<List<String>, String>(
                        tuple.getElement0().stream().sorted().collect(Collectors.toList()),
                        tuple.getElement1()))
                .collect(Collectors.toList());
        List<Tuple2<List<String>, String>> expected = new ArrayList<>();
        expected.add(new Tuple2<List<String>, String>(List.of("?om", "?pm_1", "?sm"), "?sm ?pm_1 ?om@en."));
        expected.add(new Tuple2<List<String>, String>(List.of("?om2", "?sm"),
                "?sm <http://example.com/name/> ?om2^^<http://example.com/string>."));

        assertEquals(expected, pairs);

    }

    @Test
    public void simpleTemplateSerialization() {
        MappingTuple mappingTuple = new MappingTuple();
        SolutionMapping solutionMapping = new SolutionMapping();

        solutionMapping.put("?sm", new IRINode("http://example.com/1"));
        solutionMapping.put("?pm", new IRINode("http://example.com/name"));
        solutionMapping.put("?om", new IRINode("Min Oo"));

        mappingTuple.addSolutionMap("default", solutionMapping);

        TemplateSerializer serializer = new TemplateSerializer("Serializer", "default", "?sm ?pm ?om@en.");

        MappingTuple serializedTuple = serializer.apply(mappingTuple);

        MappingTuple expected = new MappingTuple();
        SolutionMapping expectedSolutionMapping = new SolutionMapping();
        expectedSolutionMapping.put("serialized_output",
                new LiteralNode("<http://example.com/1> <http://example.com/name> \"Min Oo\"@en."));

        assertEquals(expected, serializedTuple);

    }
}

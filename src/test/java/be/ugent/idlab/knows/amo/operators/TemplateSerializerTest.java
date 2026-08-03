package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.IRINode;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.NullNode;
import be.ugent.idlab.knows.amo.operators.intermediate.unary.TemplateSerializer;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TemplateSerializerTest {

    @Test
    public void variableExtractionTest() {
        String template = """
                ?sm ?pm_1 ?om@en.
                ?sm <http://example.com/name/> ?om2^^<http://example.com/string>.
                """;
        TemplateSerializer serializer = new TemplateSerializer("Serializer", Set.of("default"), Set.of("default"), template);

        List<Pair<List<String>, String>> pairs = serializer.getVariablesTemplatePairs()
                .stream()
                .map((tuple) -> new Pair<>(
                        tuple.first().stream().sorted().collect(Collectors.toList()),
                        tuple.second()))
                .collect(Collectors.toList());
        List<Pair<List<String>, String>> expected = new ArrayList<>();
        expected.add(new Pair<>(List.of("?om", "?pm_1", "?sm"), "?sm ?pm_1 ?om@en."));
        expected.add(new Pair<>(List.of("?om2", "?sm"),
                "?sm <http://example.com/name/> ?om2^^<http://example.com/string>."));

        assertEquals(expected, pairs);

    }

    /**
     * Serializes a template with one variable bound to the given value.
     */
    private String serialize(String template, String value) {
        MappingTuple mappingTuple = new MappingTuple();
        SolutionMapping solutionMapping = new SolutionMapping();
        solutionMapping.put("?sm", new IRINode("http://example.com/1"));
        solutionMapping.put("?pm", new IRINode("http://example.com/name"));
        solutionMapping.put("?om", new LiteralNode(value));
        mappingTuple.addSolutionMap("default", solutionMapping);

        TemplateSerializer serializer =
                new TemplateSerializer("Serializer", Set.of("default"), Set.of("default"), template);

        return serializer.apply(mappingTuple).getSolutionMappings("default").iterator().next()
                .get("serialized_output").getValue().toString();
    }

    @Test
    public void valueContainingADollarIsTakenLiterally() {
        // a value is data, not a replacement pattern: '$' used to be read as a group
        // reference and threw, taking the whole mapping down
        assertEquals("<http://example.com/1> <http://example.com/name> \"costs $5\"@en.",
                serialize("?sm ?pm ?om@en.", "costs $5"));
        assertEquals("<http://example.com/1> <http://example.com/name> \"a$1b\"@en.",
                serialize("?sm ?pm ?om@en.", "a$1b"));
    }

    @Test
    public void valueContainingABackslashKeepsIt() {
        // '\' used to be read as an escape and was dropped silently
        assertEquals("<http://example.com/1> <http://example.com/name> \"back\\slash\"@en.",
                serialize("?sm ?pm ?om@en.", "back\\slash"));
    }

    @Test
    public void simpleTemplateSerialization() {

        MappingTuple mappingTuple = new MappingTuple();
        SolutionMapping solutionMapping = new SolutionMapping();
        solutionMapping.put("?sm", new IRINode("http://example.com/1"));
        solutionMapping.put("?pm", new IRINode("http://example.com/name"));
        solutionMapping.put("?om", new LiteralNode("Min Oo"));

        mappingTuple.addSolutionMap("default", solutionMapping);
        TemplateSerializer serializer = new TemplateSerializer("Serializer", Set.of("default"), Set.of("default"), "?sm ?pm ?om@en.");
        MappingTuple serializedTuple = serializer.apply(mappingTuple);
        String result = (serializedTuple.getSolutionMappings("default").iterator().next().get("serialized_output")).getValue().toString();

        assertEquals("<http://example.com/1> <http://example.com/name> \"Min Oo\"@en.", result);

    }

    @Test
    public void datatypeTemplateSerialization() {
        MappingTuple mappingTuple = new MappingTuple();
        SolutionMapping solutionMapping = new SolutionMapping();
        solutionMapping.put("?sm", new IRINode("http://example.com/10/Venus"));
        solutionMapping.put("?pm", new IRINode("http://example.com/id"));
        solutionMapping.put("?om", new LiteralNode(10, new XSDDatatype("integer")));


        mappingTuple.addSolutionMap("default", solutionMapping);

        TemplateSerializer serializer = new TemplateSerializer("Serializer", Set.of("default"), Set.of("default"), "?sm ?pm ?om .");

        MappingTuple serializedTuple = serializer.apply(mappingTuple);

        MappingTuple expected = new MappingTuple();
        SolutionMapping expectedSolutionMapping = new SolutionMapping();
        expectedSolutionMapping.put("serialized_output",
                new LiteralNode("<http://example.com/10/Venus> <http://example.com/id> \"10\"^^<http://www.w3.org/2001/XMLSchema#integer> ."));
        expected.addSolutionMap("default", expectedSolutionMapping);
        assertEquals(expected, serializedTuple);
    }

    @Test
    public void greedySerialization() {
        MappingTuple mappingTuple = new MappingTuple();
        SolutionMapping solutionMapping = new SolutionMapping();
        solutionMapping.put("?sm", new IRINode("http://example.com/10/Venus"));
        solutionMapping.put("?pm", new IRINode("http://example.com/id"));
        solutionMapping.put("?om", new LiteralNode(10, new XSDDatatype("integer")));
        solutionMapping.put("?sm_g", new IRINode("http://example.com/graph"));

        mappingTuple.addSolutionMap("default", solutionMapping);
        TemplateSerializer serializer = new TemplateSerializer("Serializer", Set.of("default"), Set.of("default"), "?sm ?pm ?om ?sm_g .");
        MappingTuple serializedTuple = serializer.apply(mappingTuple);

        String expectedQuad = "<http://example.com/10/Venus> <http://example.com/id> \"10\"^^<http://www.w3.org/2001/XMLSchema#integer> <http://example.com/graph> .";
        String actualQuad = (serializedTuple.getSolutionMappings("default").stream().findFirst().get().get("serialized_output")).getValue().toString();

        assertEquals(expectedQuad, actualQuad);
    }

    @Test
    public void skipNull() {
        MappingTuple mappingTuple = new MappingTuple();
        SolutionMapping solutionMapping = new SolutionMapping();
        solutionMapping.put("?sm", new IRINode("http://example.com/1"));
        solutionMapping.put("?pm", new IRINode("http://example.com/name"));
        solutionMapping.put("?om", new NullNode());

        mappingTuple.addSolutionMap("default", solutionMapping);
        TemplateSerializer serializer = new TemplateSerializer("Serializer", Set.of("default"), Set.of("default"), "?sm ?pm ?om@en.");
        MappingTuple serializedTuple = serializer.apply(mappingTuple);
        assertTrue((serializedTuple.getSolutionMappings("default").iterator().next().get("serialized_output")).isNull());
    }
}

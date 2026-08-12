package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.CollectionNode;
import be.ugent.idlab.knows.amo.blocks.nodes.IRINode;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.NullNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
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
    public void aVariableUsedTwiceIsFilledInBothTimes() {
        MappingTuple mappingTuple = new MappingTuple();
        SolutionMapping solutionMapping = new SolutionMapping();
        solutionMapping.put("?sm", new IRINode("http://example.com/1"));
        solutionMapping.put("?pm", new IRINode("http://example.com/knows"));
        mappingTuple.addSolutionMap("default", solutionMapping);

        TemplateSerializer serializer = new TemplateSerializer("Serializer", Set.of("default"),
                Set.of("default"), "?sm ?pm ?sm .");

        String result = serializer.apply(mappingTuple).getSolutionMappings("default").iterator().next()
                .get("serialized_output").getValue().toString();

        assertEquals("<http://example.com/1> <http://example.com/knows> <http://example.com/1> .", result);
    }

    @Test
    public void aValueThatLooksLikeAVariableIsNotFilledInAgain() {
        // the template is walked once, so a value containing "?pm" keeps it: filling the
        // variables in one at a time would substitute into the value already filled in
        MappingTuple mappingTuple = new MappingTuple();
        SolutionMapping solutionMapping = new SolutionMapping();
        solutionMapping.put("?sm", new IRINode("http://example.com/?pm"));
        solutionMapping.put("?pm", new IRINode("http://example.com/knows"));
        solutionMapping.put("?om", new LiteralNode("x"));
        mappingTuple.addSolutionMap("default", solutionMapping);

        TemplateSerializer serializer = new TemplateSerializer("Serializer", Set.of("default"),
                Set.of("default"), "?sm ?pm ?om .");

        String result = serializer.apply(mappingTuple).getSolutionMappings("default").iterator().next()
                .get("serialized_output").getValue().toString();

        assertEquals("<http://example.com/?pm> <http://example.com/knows> \"x\" .", result);
    }

    @Test
    public void aVariableIsNotFilledInsideALongerVariablesName() {
        // ?om is a prefix of ?om2, so filling ?om in first would leave "<...>2"
        MappingTuple mappingTuple = new MappingTuple();
        SolutionMapping solutionMapping = new SolutionMapping();
        solutionMapping.put("?sm", new IRINode("http://example.com/1"));
        solutionMapping.put("?om", new LiteralNode("first"));
        solutionMapping.put("?om2", new LiteralNode("second"));
        mappingTuple.addSolutionMap("default", solutionMapping);

        TemplateSerializer serializer = new TemplateSerializer("Serializer", Set.of("default"),
                Set.of("default"), "?sm <http://example.com/p> ?om2 ?om .");

        String result = serializer.apply(mappingTuple).getSolutionMappings("default").iterator().next()
                .get("serialized_output").getValue().toString();

        assertEquals("<http://example.com/1> <http://example.com/p> \"second\" \"first\" .", result);
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

    /**
     * A collection of literals, as a function producing several values returns.
     */
    private static CollectionNode collectionOf(String... values) {
        return new CollectionNode(List.of(values).stream().map(v -> (RDFNode) new LiteralNode(v)).toList());
    }

    /**
     * Serializes "?sm ?pm ?om ." with the given values, ?om holding a collection.
     */
    private String serializeCollection(String... values) {
        MappingTuple mappingTuple = new MappingTuple();
        SolutionMapping solutionMapping = new SolutionMapping();
        solutionMapping.put("?sm", new IRINode("http://example.com/1"));
        solutionMapping.put("?pm", new IRINode("http://example.com/name"));
        solutionMapping.put("?om", collectionOf(values));
        mappingTuple.addSolutionMap("default", solutionMapping);

        TemplateSerializer serializer = new TemplateSerializer("Serializer", Set.of("default"),
                Set.of("default"), "?sm ?pm ?om .");

        return serializer.apply(mappingTuple).getSolutionMappings("default").iterator().next()
                .get("serialized_output").getValue().toString();
    }

    @Test
    public void aCollectionGivesAStatementPerMember() {
        assertEquals("""
                        <http://example.com/1> <http://example.com/name> "read" .
                        <http://example.com/1> <http://example.com/name> "write" .""",
                serializeCollection("read", "write"));
    }

    @Test
    public void aCollectionOfOneGivesTheStatementItsMemberDoes() {
        assertEquals("<http://example.com/1> <http://example.com/name> \"read\" .",
                serializeCollection("read"));
    }

    @Test
    public void anEmptyCollectionStatesNothing() {
        MappingTuple mappingTuple = new MappingTuple();
        SolutionMapping solutionMapping = new SolutionMapping();
        solutionMapping.put("?sm", new IRINode("http://example.com/1"));
        solutionMapping.put("?pm", new IRINode("http://example.com/name"));
        solutionMapping.put("?om", new CollectionNode(List.of()));
        mappingTuple.addSolutionMap("default", solutionMapping);

        TemplateSerializer serializer = new TemplateSerializer("Serializer", Set.of("default"),
                Set.of("default"), "?sm ?pm ?om .");

        assertTrue(serializer.apply(mappingTuple).getSolutionMappings("default").iterator().next()
                .get("serialized_output").isNull());
    }

    @Test
    public void twoCollectionsMultiplyOut() {
        // both variables stand for several terms, so every pairing of them is stated
        MappingTuple mappingTuple = new MappingTuple();
        SolutionMapping solutionMapping = new SolutionMapping();
        solutionMapping.put("?sm", new IRINode("http://example.com/1"));
        solutionMapping.put("?pm", collectionOf("read", "write"));
        solutionMapping.put("?om", collectionOf("a", "b"));
        mappingTuple.addSolutionMap("default", solutionMapping);

        TemplateSerializer serializer = new TemplateSerializer("Serializer", Set.of("default"),
                Set.of("default"), "?sm ?pm ?om .");

        String result = serializer.apply(mappingTuple).getSolutionMappings("default").iterator().next()
                .get("serialized_output").getValue().toString();

        assertEquals("""
                <http://example.com/1> "read" "a" .
                <http://example.com/1> "read" "b" .
                <http://example.com/1> "write" "a" .
                <http://example.com/1> "write" "b" .""", result);
    }

    @Test
    public void aCollectionVariableUsedTwiceTakesTheSameMemberBothTimes() {
        // the variable stands for one term at a time, so a statement pairs a member with
        // itself rather than with every other member
        MappingTuple mappingTuple = new MappingTuple();
        SolutionMapping solutionMapping = new SolutionMapping();
        solutionMapping.put("?sm", new IRINode("http://example.com/1"));
        solutionMapping.put("?om", collectionOf("a", "b"));
        mappingTuple.addSolutionMap("default", solutionMapping);

        TemplateSerializer serializer = new TemplateSerializer("Serializer", Set.of("default"),
                Set.of("default"), "?sm ?om ?om .");

        String result = serializer.apply(mappingTuple).getSolutionMappings("default").iterator().next()
                .get("serialized_output").getValue().toString();

        assertEquals("""
                <http://example.com/1> "a" "a" .
                <http://example.com/1> "b" "b" .""", result);
    }

    @Test
    public void aCollectionMemberContainingADollarIsTakenLiterally() {
        // every member goes through the same quoting a single value does
        assertEquals("""
                        <http://example.com/1> <http://example.com/name> "costs $5" .
                        <http://example.com/1> <http://example.com/name> "back\\slash" .""",
                serializeCollection("costs $5", "back\\slash"));
    }
}

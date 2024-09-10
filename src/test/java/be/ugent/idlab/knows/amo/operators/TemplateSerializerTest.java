package be.ugent.idlab.knows.amo.operators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.IRINode;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.operators.intermediate.unary.TemplateSerializer;

public class TemplateSerializerTest {

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

package be.ugent.idlab.knows.amo.blocks;

import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.core.Quad;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BGPTest {

    @Test
    public void fillTriple() {
        String pattern = "?sub ?pred ?obj .";
        BGP bgp = new BGP(pattern);

        SolutionMapping mapping = new SolutionMapping(Map.of(
                "?sub", new LiteralNode("subject"),
                "?pred", new LiteralNode("predicate"),
                "?obj", new LiteralNode("object")
        ));

        DatasetGraph actual = bgp.apply(mapping);
        DatasetGraph expected = DatasetGraphFactory.create();
        expected.getDefaultGraph().add(
                NodeFactory.createLiteral("subject"),
                NodeFactory.createLiteral("predicate"),
                NodeFactory.createLiteral("object")
        );

        List<Quad> actualQuads = actual.stream().toList();
        List<Quad> expectedQuads = expected.stream().toList();
        assertEquals(expectedQuads, actualQuads);
    }

    @Test
    public void fillQuad() {
        String pattern = "?sub ?pred ?obj ?graph .";
        BGP bgp = new BGP(pattern);

        SolutionMapping mapping = new SolutionMapping(Map.of(
                "?sub", new LiteralNode("subject"),
                "?pred", new LiteralNode("predicate"),
                "?obj", new LiteralNode("object"),
                "?graph", new LiteralNode("graph")
        ));

        DatasetGraph expected = DatasetGraphFactory.create();
        expected.add(NodeFactory.createLiteral("graph"),
                NodeFactory.createLiteral("subject"),
                NodeFactory.createLiteral("predicate"),
                NodeFactory.createLiteral("object")
        );

        DatasetGraph actual = bgp.apply(mapping);
        List<Quad> actualQuads = actual.stream().toList();
        List<Quad> expectedQuads = expected.stream().toList();

        assertEquals(expectedQuads, actualQuads);
    }

    @Test
    public void subjectVariableWithLanguageTag() {
        String pattern = "?sub ?pred ?obj@en .";
        BGP bgp = new BGP(pattern);

        SolutionMapping mapping = new SolutionMapping(Map.of(
                "?sub", new LiteralNode("subject"),
                "?pred", new LiteralNode("predicate"),
                "?obj", new LiteralNode("object" , "string", "en")
        ));

        DatasetGraph expected = DatasetGraphFactory.create();
        expected.getDefaultGraph().add(
                NodeFactory.createLiteral("subject"),
                NodeFactory.createLiteral("predicate"),
                NodeFactory.createLiteral("object", "en")
        );

        DatasetGraph actual = bgp.apply(mapping);
        List<Quad> actualQuads = actual.stream().toList();
        List<Quad> expectedQuads = expected.stream().toList();

        assertEquals(expectedQuads, actualQuads);
    }
}

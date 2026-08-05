package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.BGP;
import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.BlankNode;
import be.ugent.idlab.knows.amo.operators.intermediate.unary.SerializeOperator;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SerializeOperatorTest {

    @Test
    public void simpleTest() {
        String bgp = "?firstname_iri <http://example.com/name> ?fullname .";

        MappingTuple input = BlocksIO.readMappingTuple("operators/serialize/input.json");

        SerializeOperator operator = new SerializeOperator("SerializeOp", Set.of("f_contacts"), Set.of("f_contacts"), new BGP(bgp), "NQ");

        MappingTuple actual = operator.apply(input);
        MappingTuple expected = BlocksIO.readMappingTuple("operators/serialize/output.json");

        assertEquals(expected, actual);
    }

    @Test
    public void everyBlankNodeLosesJenasPrefix() {
        // Jena writes blank node labels with a 'B' in front, which is stripped again. A
        // solution mapping can hold several of them, so every one has to be stripped, not
        // just the first.
        String bgp = "?person <http://example.com/knows> ?friend .\n"
                + "?friend <http://example.com/knows> ?colleague .";

        SolutionMapping mapping = new SolutionMapping();
        mapping.put("?person", new BlankNode("person1"));
        mapping.put("?friend", new BlankNode("friend1"));
        mapping.put("?colleague", new BlankNode("colleague1"));

        MappingTuple input = new MappingTuple();
        input.addSolutionMap("f_contacts", mapping);

        SerializeOperator operator = new SerializeOperator("SerializeOp", Set.of("f_contacts"),
                Set.of("f_contacts"), new BGP(bgp), "NQ");

        String serialized = operator.apply(input).getSolutionMappings("f_contacts").iterator().next()
                .get("?serialized_output").getValue().toString();

        assertFalse(serialized.contains("_:B"), "no blank node keeps Jena's prefix: " + serialized);
        assertTrue(serialized.contains("_:person1"), serialized);
        assertTrue(serialized.contains("_:friend1"), serialized);
        assertTrue(serialized.contains("_:colleague1"), serialized);
    }

    @Test
    public void emptyMap() {
        String bgp = "?firstname_iri <http://example.com/name> ?fullname .";
        MappingTuple input = new MappingTuple();

        SerializeOperator op = new SerializeOperator("SerializeOp", Set.of("f_contacts"), Set.of("f_contacts"), new BGP(bgp), "NQ");
        MappingTuple actual = op.apply(input);

        assertTrue(actual.getFragments().isEmpty());
        assertTrue(actual.getMap().isEmpty());
    }
}

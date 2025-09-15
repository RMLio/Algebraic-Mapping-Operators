package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.BGP;
import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.operators.intermediate.unary.SerializeOperator;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void emptyMap() {
        String bgp = "?firstname_iri <http://example.com/name> ?fullname .";
        MappingTuple input = new MappingTuple();

        SerializeOperator op = new SerializeOperator("SerializeOp", Set.of("f_contacts"), Set.of("f_contacts"), new BGP(bgp), "NQ");
        MappingTuple actual = op.apply(input);

        assertTrue(actual.getFragments().isEmpty());
        assertTrue(actual.getMap().isEmpty());
    }
}

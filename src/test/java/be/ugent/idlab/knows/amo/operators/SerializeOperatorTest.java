package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.BGP;
import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SerializeOperatorTest {

    @Test
    public void simpleTest() {
        String bgp = "?firstname_iri <http://example.com/name> ?fullname;<http://example.com/petName> ?pet_name.";

        MappingTuple input = BlocksIO.readMappingTuple("operators/serialize/input.json");

        SerializeOperator operator = new SerializeOperator(new BGP(bgp), "TTL");

        MappingTuple actual = operator.apply(input);
        MappingTuple expected = BlocksIO.readMappingTuple("operators/serialize/output.json");

        assertEquals(expected, actual);
    }
}

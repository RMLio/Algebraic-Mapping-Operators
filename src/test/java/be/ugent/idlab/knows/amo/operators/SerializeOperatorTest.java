package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.BGP;
import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SerializeOperatorTest {

    @Test
    public void simpleTest() {
        String bgp = "?firstname_iri <http://example.com/name> ?fullname;<http://example.com/petName> ?pet_name.";

        SolutionMapping solMapping = new SolutionMapping(Map.of(
                "?fullname", "John Doe",
                "?$pet.type", "dog",
                "?pet_name", "Max",
                "?firstname_iri", "http://example.com/John",
                "?$pet.age", 10
        ));

        MappingTuple tuple = new MappingTuple();
        tuple.setSolutionMap("f_contacts", Collections.singleton(solMapping));

        SerializeOperator operator = new SerializeOperator(new BGP(bgp));

        MappingTuple out = operator.apply(tuple);

        List<String> fragments = out.getFragments().stream().toList();
        assertEquals(1, fragments.size());
        assertEquals("f_contacts", fragments.get(0));

        List<SolutionMapping> solMappings = out.getSolutionMappings("f_contacts").stream().toList();
        assertEquals(1, solMappings.size());
        SolutionMapping mapping = solMappings.get(0);

        assertTrue(mapping.containsKey("?serialized_output"));
        String serialized = """
                <http://example.com/John>
                        <http://example.com/name>     "John Doe";
                        <http://example.com/petName>  "Max" .
                """;
        assertEquals(serialized, mapping.get("?serialized_output"));
    }
}

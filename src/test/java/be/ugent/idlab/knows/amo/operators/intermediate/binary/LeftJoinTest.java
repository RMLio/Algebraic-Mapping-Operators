package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.intermediate.binary.LeftJoin;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LeftJoinTest {
    @Test
    public void simpleSolutionMappingTest() {
        LeftJoin operator = new LeftJoin(
                (s1, s2) -> s1.containsKey("?name") &&
                        s2.containsKey("?owner") &&
                        s1.get("?name").equals(s2.get("?pet.owner"))
        );

        SolutionMapping owner = new SolutionMapping(Map.of(
                "?name", "John Doe",
                "?age", 50,
                "?workplace", "Ghent"
        ));

        SolutionMapping pet = new SolutionMapping(Map.of(
                "?pet.name", "Max",
                "?pet.owner", "John Doe"
        ));

        SolutionMapping result = operator.applySolMapping(owner, pet);

        assertEquals(5, result.keySet().size());
        assertEquals("John Doe", result.get("?name"));
        assertEquals(50, result.get("?age"));
        assertEquals("Ghent", result.get("?workplace"));
        assertEquals("Max", result.get("?pet.name"));
        assertEquals("John Doe", result.get("?pet.owner"));
    }

    @Test
    public void simpleMappingTupleTest() {
        LeftJoin operator = new LeftJoin(
                (s1, s2) -> s1.containsKey("?name") &&
                        s2.containsKey("?pet.owner") &&
                        s1.get("?name").equals(s2.get("?pet.owner"))
        );

        SolutionMapping pet = new SolutionMapping(Map.of(
                "?pet.name", "Max",
                "?pet.owner", "John Doe"
        ));
        MappingTuple pets = new MappingTuple();
        pets.setSolutionMap("default", List.of(pet));

        SolutionMapping johnDoe = new SolutionMapping(Map.of(
                "?name", "John Doe",
                "?age", 50,
                "?workplace", "Ghent"
        ));

        SolutionMapping janeDoe = new SolutionMapping(Map.of(
                "?name", "Jane Doe",
                "?age", 25,
                "?workplace", "Antwerp"
        ));
        MappingTuple owners = new MappingTuple();
        owners.setSolutionMap("default", List.of(johnDoe, janeDoe));

        MappingTuple result = operator.applyMapTuple(owners, pets);
        assertEquals(1, result.getFragments().size());

        List<SolutionMapping> mappings = result.getSolutionMappings("default").stream().toList();
        SolutionMapping john = mappings.get(0);

        assertEquals(5, john.keySet().size());
        assertEquals("John Doe", john.get("?name"));
        assertEquals(50, john.get("?age"));
        assertEquals("Ghent", john.get("?workplace"));
        assertEquals("Max", john.get("?pet.name"));
        assertEquals("John Doe", john.get("?pet.owner"));

        SolutionMapping jane = mappings.get(1);
        assertEquals(5, jane.keySet().size());
        assertEquals("Jane Doe", jane.get("?name"));
        assertEquals(25, jane.get("?age"));
        assertEquals("Antwerp", jane.get("?workplace"));
        assertNull(jane.get("?pet.name"));
        assertNull(jane.get("?pet.owner"));
    }
}

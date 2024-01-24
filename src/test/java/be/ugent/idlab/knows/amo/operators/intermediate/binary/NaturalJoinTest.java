package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.intermediate.binary.NaturalJoin;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class NaturalJoinTest {

    NaturalJoin operator = new NaturalJoin();

    @Test
    public void simpleTest() {
        // setup
        SolutionMapping john = new SolutionMapping(Map.of(
                "?name", "John Doe",
                "?$pet.type", "dog",
                "?$pet.name", "Bax",
                "?firstname", "John",
                "?lastname", "Doe"
        ));

        SolutionMapping susan = new SolutionMapping(Map.of(
                "?name", "Susan Sue",
                "?firstname", "Susan",
                "?lastname", "Sue"
        ));

        MappingTuple owners = new MappingTuple();
        owners.setSolutionMap("f_contacts", List.of(john, susan));

        SolutionMapping bax = new SolutionMapping(Map.of(
                "?$type", "dog",
                "?$pet.name", "Bax",
                "?$pet.age", 10
        ));

        SolutionMapping coco = new SolutionMapping(Map.of(
                "?$type", "cat",
                "?$pet.name", "Coco",
                "?$pet.age", 3
        ));

        SolutionMapping max = new SolutionMapping(Map.of(
                "?$type", "dog",
                "?$pet.name", "max",
                "?$pet.age", 5
        ));

        MappingTuple pets = new MappingTuple();
        pets.setSolutionMap("f_contacts", List.of(bax, coco, max));

        // test
        MappingTuple result = operator.applyMapTuple(owners, pets);

        // verification
        Collection<String> fragments = result.getFragments();
        assertEquals(1, fragments.size());

        Optional<String> firstFragment = fragments.stream().findFirst();
        assertTrue(firstFragment.isPresent());
        assertEquals("f_contacts", firstFragment.get());

        Collection<SolutionMapping> solutions = result.getSolutionMappings("f_contacts");
        assertFalse(solutions.isEmpty());
        SolutionMapping sol = solutions.stream().findFirst().get();

        assertEquals(7, sol.keySet().size());
        // verify all fields are present
        assertEquals("John Doe", sol.get("?name"));
        assertEquals("dog", sol.get("?$pet.type"));
        assertEquals("Bax", sol.get("?$pet.name"));
        assertEquals("John", sol.get("?firstname"));
        assertEquals("Doe", sol.get("?lastname"));
        assertEquals(10, sol.get("?$pet.age"));
        assertEquals("dog", sol.get("?$type"));
    }
}

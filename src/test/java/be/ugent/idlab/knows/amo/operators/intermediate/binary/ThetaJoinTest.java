package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.intermediate.binary.ThetaJoin;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThetaJoinTest {

    ThetaJoin operator = new ThetaJoin(((s1, s2) -> {
        if (s1.containsKey("?$pet.type") && s2.containsKey("?$type")) {
            return s1.get("?$pet.type").equals(s2.get("?$type"));
        }
        return false;
    }));

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
                "?$pet.name", "Max",
                "?$pet.age", 5
        ));

        MappingTuple pets = new MappingTuple();
        pets.setSolutionMap("f_contacts", List.of(bax, coco, max));


        SolutionMapping unionBax = new SolutionMapping(Map.of(
                "?name", "John Doe",
                "?$pet.type", "dog",
                "?$pet.name", "Bax",
                "?firstname", "John",
                "?lastname", "Doe",
                "?$pet.age", 10,
                "?$type", "dog"
        ));

        SolutionMapping unionMax = new SolutionMapping(Map.of(
                "?name", "John Doe",
                "?$pet.type", "dog",
                "?$pet.name", "Max",
                "?firstname", "John",
                "?lastname", "Doe",
                "?$pet.age", 5,
                "?$type", "dog"
        ));

        MappingTuple expected = new MappingTuple();
        expected.setSolutionMap("f_contacts", List.of(unionBax, unionMax));

        // test
        MappingTuple out = operator.applyMapTuple(owners, pets);

        // validation
        Collection<String> fragments = out.getFragments();
        assertEquals(1, fragments.size());
        assertTrue(fragments.stream().findFirst().isPresent());
        assertEquals("f_contacts", fragments.stream().findFirst().get());

        List<SolutionMapping> mappings = out.getSolutionMappings("f_contacts").stream().toList();
        SolutionMapping m0 = mappings.get(0);
        assertEquals(unionBax, m0);

        SolutionMapping m1 = mappings.get(1);
        assertEquals(unionMax, m1);
    }
}

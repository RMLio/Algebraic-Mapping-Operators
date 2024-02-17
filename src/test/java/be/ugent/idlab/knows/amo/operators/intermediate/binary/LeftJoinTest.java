package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Disabled // waiting for go-ahead
public class LeftJoinTest {
    @Test
    public void paperTest() {
        // table 6
        SolutionMapping john = new SolutionMapping(Map.of(
                "?fullname", "John Doe",
                "?$pet.type", "dog",
                "?$pet.name", "Bax",
                "?firstname_iri", "http://example.com/John"
        ));

        // different construction used due to null values present
        SolutionMapping susan = new SolutionMapping(new HashMap<>() {{
            put("?fullname", "Susan Sue");
            put("?$pet.type", null);
            put("?$pet.name", null);
            put("?firstname_iri", "http://example.com/Susan");
        }});


        MappingTuple table6 = new MappingTuple();
        table6.setSolutionMaps("f_contacts", john, susan);

        // table 7
        SolutionMapping bax = new SolutionMapping(Map.of(
                "?type", "dog",
                "?name", "Bax",
                "?age", 10
        ));
        SolutionMapping coco = new SolutionMapping(Map.of(
                "?type", "cat",
                "?name", "Coco",
                "?age", 3
        ));
        SolutionMapping max = new SolutionMapping(Map.of(
                "?type", "dog",
                "?name", "Max",
                "?age", 5
        ));

        MappingTuple table7 = new MappingTuple();
        table7.setSolutionMaps("f_contacts", bax, coco, max);

        LeftJoin operator = new LeftJoin(((s1, s2) ->
                s1.containsKey("?$pet.type") && s1.get("?$pet.type") != null &&
                        s2.containsKey("?type") && s2.get("?type") != null &&
                        s1.get("?$pet.type").equals(s2.get("?type"))));

        MappingTuple out = operator.applyMapTuple(table6, table7);

        assertEquals(5, out.getSolutionMappings("f_contacts").size());

        // for mappings satisfying the condition, must behave exactly as ThetaJoin
        Collection<SolutionMapping> solMappings = out.getSolutionMappings("f_contacts");
        SolutionMapping baxMapping = solMappings.stream().filter(s -> s.get("?name").equals("Bax")).toList().get(0);
        SolutionMapping maxMapping = solMappings.stream().filter(s -> s.get("?name").equals("Max")).toList().get(0);

        // verify all fields
        assertEquals(7, baxMapping.size());
        assertEquals("John Doe", baxMapping.get("?fullname"));
        assertEquals("dog", baxMapping.get("?$pet.type"));
        assertEquals(10, baxMapping.get("?age"));
        assertEquals("dog", baxMapping.get("?type"));
        assertEquals("Bax", baxMapping.get("?name"));
        assertEquals("http://example.com/John", baxMapping.get("?firstname_iri"));
        assertEquals("Bax", baxMapping.get("?$pet.name"));

        assertEquals(7, maxMapping.size());
        assertEquals("John Doe", maxMapping.get("?fullname"));
        assertEquals("dog", maxMapping.get("?$pet.type"));
        assertEquals(5, maxMapping.get("?age"));
        assertEquals("dog", maxMapping.get("?type"));
        assertEquals("Max", maxMapping.get("?name"));
        assertEquals("Bax", maxMapping.get("?$pet.name"));
        assertEquals("http://example.com/John", maxMapping.get("?firstname_iri"));

        // for mapping failing the condition, all keys must be present, but foreign keys must be null
        Collection<SolutionMapping> failingMappings = out.getSolutionMappings("f_contacts")
                .stream().filter(s -> !s.get("?fullname").equals("John Doe")).toList();
        assertEquals(3, failingMappings.size());

        assertTrue(failingMappings.stream().allMatch(s -> s.containsKey("?fullname") && s.get("?fullname").equals("Susan Sue")));
        assertTrue(failingMappings.stream().allMatch(s -> s.containsKey("?firstname_iri") && s.get("?firstname_iri").equals("http://example.com/Susan")));

        assertTrue(failingMappings.stream().allMatch(s -> s.containsKey("?$pet.type") && s.get("?$pet.type") == null));
        assertTrue(failingMappings.stream().allMatch(s -> s.containsKey("?$pet.name") && s.get("?$pet.name") == null));
        assertTrue(failingMappings.stream().allMatch(s -> s.containsKey("?type") && s.get("?type") == null));
        assertTrue(failingMappings.stream().allMatch(s -> s.containsKey("?name") && s.get("?name") == null));
        assertTrue(failingMappings.stream().allMatch(s -> s.containsKey("?age") && s.get("?age") == null));





    }
}

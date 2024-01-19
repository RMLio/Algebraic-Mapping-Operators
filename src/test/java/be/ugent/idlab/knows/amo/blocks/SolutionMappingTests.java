package be.ugent.idlab.knows.amo.blocks;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolutionMappingTests {

    /**
     * A simple smoke test of applying a string to a mapping tuple
     */
    @Test
    public void simpleStringApply() {
        SolutionMapping mapping = new SolutionMapping(Map.of(
           "?age", 25,
           "?name", "John Doe"
        ));

        String s = "?name ; ?age";

        String expected = "John Doe ; 25";
        assertEquals(expected, mapping.apply(s));
    }

    @Test
    public void unionTest() {
        SolutionMapping m1 = new SolutionMapping(Map.of(
           "foo", 1
        ));

        SolutionMapping m2 = new SolutionMapping(Map.of(
                "bar", 2
        ));

        SolutionMapping merged = m1.union(m2);
        assertEquals(2, merged.keySet().size());
    }
}

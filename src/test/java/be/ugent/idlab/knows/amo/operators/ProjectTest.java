package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectTest {
    @Test
    public void simpleTestSolutionMapping() {
        SolutionMapping mapping = new SolutionMapping(
                new HashMap<>() {
                    {
                        put("?name", "John Doe");
                        put("?age", 25);
                    }
                }
        );

        ProjectOperator project = new ProjectOperator();

        SolutionMapping expected = new SolutionMapping(
                Map.of("?name", "John Doe"));
        SolutionMapping actual = project.apply(mapping, List.of("?age"));

        assertEquals(expected, actual);
    }
}

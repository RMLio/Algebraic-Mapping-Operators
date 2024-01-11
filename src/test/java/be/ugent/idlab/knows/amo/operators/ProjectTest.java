package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectTest {
    @Test
    public void simpleTestSolutionMapping() {
        SolutionMapping mapping = new SolutionMapping(
                new HashMap<>() {{
                    put("?name", "John Doe");
                    put("?age", 25);
                }}
        );

        ProjectOperator project = new ProjectOperator();

        SolutionMapping expected = new SolutionMapping(
                Map.of("?name", "John Doe"));
        SolutionMapping actual = project.applySolMapping(mapping, List.of("?age"));

        assertEquals(expected, actual);
    }

    @Test
    public void simpleTestMappingTuple() {
        SolutionMapping mapping = new SolutionMapping(
                new HashMap<>() {{
                    put("?name", "John Doe");
                    put("?age", 25);
                }}
        );

        MappingTuple actual = new MappingTuple();
        actual.addSolutionMap("f_default", mapping);

        ProjectOperator projectOperator = new ProjectOperator();
        projectOperator.applyMapTuple(actual, List.of("?age"));

        MappingTuple expected = new MappingTuple();
        expected.addSolutionMap("f_default", new SolutionMapping(Map.of("?name", "John Doe")));

        assertEquals(expected, actual);
    }
}

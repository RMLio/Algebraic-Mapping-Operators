package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
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
                new HashMap<>() {{
                    put("?name", "John Doe");
                    put("?age", 25);
                }}
        );

        ProjectOperator project = new ProjectOperator(List.of("?age"));

        SolutionMapping expected = new SolutionMapping(
                Map.of("?name", "John Doe"));
        SolutionMapping actual = project.applySolMapping(mapping);

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

        ProjectOperator projectOperator = new ProjectOperator(List.of("?age"));
        projectOperator.applyMappingTuple(actual);

        MappingTuple expected = new MappingTuple();
        expected.addSolutionMap("f_default", new SolutionMapping(Map.of("?name", "John Doe")));

        assertEquals(expected, actual);
    }
}

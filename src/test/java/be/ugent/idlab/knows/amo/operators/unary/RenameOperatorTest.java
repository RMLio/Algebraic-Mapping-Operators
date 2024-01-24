package be.ugent.idlab.knows.amo.operators.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RenameOperatorTest {

    @Test
    public void simpleTestSolutionMapping() {
        SolutionMapping mapping1 = new SolutionMapping(Map.of(
                "?name", "John Doe"
        ));

        RenameOperator operator = new RenameOperator(Set.of(new Pair("?name", "?fullname")));
        SolutionMapping out = operator.applySolMapping(mapping1);

        assertEquals(1, out.keySet().size());
        assertEquals("John Doe", out.get("?fullname"));
        assertNull(out.get("?name"));
    }

    @Test
    public void simpleTestMappingTuple() {
        SolutionMapping mapping1 = new SolutionMapping(Map.of(
                "?name", "John Doe"
        ));
        MappingTuple tuple = new MappingTuple();

        tuple.setSolutionMap("f_default", Collections.singleton(mapping1));

        RenameOperator operator = new RenameOperator(Set.of(new Pair("?name", "?fullname")));

        MappingTuple actual = operator.applyMappingTuple(tuple);

        assertEquals(1, actual.getSolutionMappings("f_default").size());
        Optional<SolutionMapping> actualMapping = actual.getSolutionMappings("f_default").stream().findFirst();
        assertTrue(actualMapping.isPresent());
        assertEquals("John Doe", actualMapping.get().get("?fullname"));
    }
}

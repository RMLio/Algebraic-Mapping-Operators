package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.unary.ExtendOperator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExtendTest {

    // simple function to concatenate first and last names
    ExtendOperator op = new ExtendOperator("?full_name", (m) -> {
        String first = (String) m.get("?first_name");
        String last = (String) m.get("?last_name");

        return first + " " + last;
    });


    @Test
    public void simpleTestSolMapping() {
        SolutionMapping mapping = new SolutionMapping(
                new HashMap<>() {{
                    put("?first_name", "John");
                    put("?last_name", "Doe");
                    put("?age", 25);
                }}
        );

        op.applySolMapping(mapping);

        SolutionMapping expected = new SolutionMapping(
                new HashMap<>() {{
                    put("?first_name", "John");
                    put("?last_name", "Doe");
                    put("?full_name", "John Doe");
                    put("?age", 25);
                }});

        assertEquals(expected, mapping);
    }

    @Test
    public void simpleTestMappingTuple() {
        SolutionMapping solmap = new SolutionMapping(
                new HashMap<>() {{
                    put("?first_name", "John");
                    put("?last_name", "Doe");
                    put("?age", 25);
                }}
        );
        MappingTuple tuple = new MappingTuple();
        tuple.addSolutionMap("f_default", solmap);

        MappingTuple expected = new MappingTuple();
        expected.addSolutionMap("f_default",
                new SolutionMapping(
                        Map.of(
                                "?first_name", "John",
                                "?last_name", "Doe",
                                "?age", 25,
                                "?full_name", "John Doe"
                        )
                )
        );


        MappingTuple actual = new MappingTuple();
        actual.addSolutionMap("f_default", solmap);
    }
}

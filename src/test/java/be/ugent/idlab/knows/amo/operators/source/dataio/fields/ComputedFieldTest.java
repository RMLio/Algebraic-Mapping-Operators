package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.ExtendFunction;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Verifies that a field carrying an expression (a function on the record data) applies the
 * function to the referenced column and binds the result to the field's variable, e.g.
 * {@code toUpperCase(name)} on a one-column CSV.
 */
public class ComputedFieldTest {

    /** toUpperCase(name) */
    private static final ExtendFunction TO_UPPER_CASE = solutionMapping -> {
        if (solutionMapping == null || solutionMapping.get("name") == null) {
            return null;
        }
        return solutionMapping.get("name").getValue().toString().toUpperCase();
    };

    @Test
    public void appliesFunctionToColumnAndBindsResultToFieldName() {
        Field field = Field.builder()
                .withName("Name")
                .CSV()
                .withExpression(TO_UPPER_CASE)
                .build();

        List<SolutionMapping> result = field.apply(Optional.of("name\nmatthieu\n"));

        assertEquals(1, result.size());
        assertEquals("MATTHIEU", result.get(0).get("Name").getValue().toString());
        // the raw column is not leaked as a variable, only the computed field
        assertNull(result.get(0).get("name"));
    }

    @Test
    public void plainReferenceFieldIsUnaffected() {
        Field field = Field.builder()
                .withName("Name")
                .CSV()
                .withReference("name")
                .build();

        List<SolutionMapping> result = field.apply(Optional.of("name\nmatthieu\n"));

        assertEquals(1, result.size());
        assertEquals("matthieu", result.get(0).get("Name").getValue().toString());
    }
}

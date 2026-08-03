package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A reference that matches nothing is a data error, not a mapping error: the field binds
 * no value and the rest of the record is still read. JSONPath signals it by throwing
 * ({@code PathNotFoundException}, since paths are read with {@code REQUIRE_PROPERTIES}),
 * so it has to be caught rather than allowed to abort the mapping.
 */
public class MissingReferenceTest {

    private static final String RECORD = "{\"name\": \"alice\", \"items\": [{\"type\": \"sword\"}]}";

    @Test
    public void referenceToAMissingPropertyBindsNothing() {
        List<SolutionMapping> result =
                Field.builder().JSON().withName("x").withReference("$.missing").build()
                        .apply(Optional.of(RECORD));

        assertEquals(1, result.size());
        assertTrue(result.get(0).get("x").isNull());
    }

    @Test
    public void referenceIntoAMissingNestedPropertyBindsNothing() {
        List<SolutionMapping> result =
                Field.builder().JSON().withName("x").withReference("$.items[0].missing").build()
                        .apply(Optional.of(RECORD));

        assertEquals(1, result.size());
        assertTrue(result.get(0).get("x").isNull());
    }

    @Test
    public void anIteratorMatchingNothingProducesNoRecords() {
        List<SolutionMapping> result =
                new IteratorField("it", List.of(), ReferenceFormulation.JSONPath, "$.missing[*]")
                        .apply(Optional.of(RECORD));

        assertTrue(result.isEmpty());
    }

    @Test
    public void subfieldsOfAnIteratorMatchingNothingBindNothing() {
        Field type = Field.builder().JSON().withName("type").withReference("$.type").build();

        List<SolutionMapping> result =
                new IteratorField("it", List.of(type), ReferenceFormulation.JSONPath, "$.missing[*]")
                        .apply(Optional.of(RECORD));

        assertEquals(1, result.size());
        assertTrue(result.get(0).get("it.type").isNull());
    }

    @Test
    public void aSubfieldReferenceThatMatchesNothingBindsNothing() {
        Field weight = Field.builder().JSON().withName("weight").withReference("$.weight").build();

        List<SolutionMapping> result =
                new IteratorField("it", List.of(weight), ReferenceFormulation.JSONPath, "$.items[*]")
                        .apply(Optional.of(RECORD));

        assertEquals(1, result.size());
        assertTrue(result.get(0).get("it.weight").isNull());
    }
}

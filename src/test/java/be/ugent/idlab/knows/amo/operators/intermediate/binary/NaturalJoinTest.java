package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NaturalJoinTest {

    NaturalJoinOperator operator = new NaturalJoinOperator("NatJoin", Set.of("f_default"), Set.of("f_default"));

    @Test
    public void simpleTest() {
        MappingTuple owners = BlocksIO.readMappingTuple("operators/naturalJoin/owners.json");
        MappingTuple pets = BlocksIO.readMappingTuple("operators/naturalJoin/pets.json");
        MappingTuple expected = BlocksIO.readMappingTuple("operators/naturalJoin/output.json");
        MappingTuple result = operator.apply(owners, pets);
        assertEquals(expected, result);
    }

    /**
     * A join compares values, and a collection is a value like any other: it joins a
     * collection holding the same terms in the same order, and nothing else.
     * <p>
     * ?tag is a collection of "read" and "write" for John and the plain literal "read" for
     * Susan. Only John's collection joins the identical one; the literal "read" is a term
     * the collection stands for, not the collection, and a collection listing the same
     * terms in another order is another value. Matching a collection against the terms it
     * stands for is a different join condition, which a mapping plan has to ask for.
     */
    @Test
    public void aCollectionJoinsAnEqualCollectionAndNothingElse() {
        MappingTuple owners = BlocksIO.readMappingTuple("operators/naturalJoin/collections/owners.json");
        MappingTuple permissions = BlocksIO.readMappingTuple("operators/naturalJoin/collections/permissions.json");
        MappingTuple expected = BlocksIO.readMappingTuple("operators/naturalJoin/collections/output.json");

        MappingTuple result = operator.apply(owners, permissions);

        // the number of joined mappings is asserted as well: a mapping tuple compares equal
        // as long as the expected mappings are found in it, so an extra join result would
        // go unnoticed. A pair that does not join is stored as a null.
        long joined = result.getSolutionMappings("f_default").stream().filter(Objects::nonNull).count();
        assertEquals(1, joined);
        assertEquals(expected, result);
    }
}

package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NaturalJoinTest {

    NaturalJoin operator = new NaturalJoin();

    @Test
    public void simpleTest() {
        MappingTuple owners = BlocksIO.readMappingTuple("operators/naturalJoin/owners.json");
        MappingTuple pets = BlocksIO.readMappingTuple("operators/naturalJoin/pets.json");
        MappingTuple expected = BlocksIO.readMappingTuple("operators/naturalJoin/output.json");
        MappingTuple result = operator.apply(owners, pets);
        assertEquals(expected, result);
    }
}

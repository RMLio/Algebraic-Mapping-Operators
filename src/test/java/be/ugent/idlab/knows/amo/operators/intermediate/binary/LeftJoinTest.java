package be.ugent.idlab.knows.amo.operators.intermediate.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.functions.JoinCondition;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled // waiting for go-ahead
public class LeftJoinTest {
    @Test
    public void paperTest() {
        // TODO: This test needs to be checked and fixed!
        MappingTuple expected = BlocksIO.readMappingTuple("operators/leftJoin/paperTest/output.json");

        MappingTuple table6 = BlocksIO.readMappingTuple("operators/leftJoin/paperTest/table6.json");
        MappingTuple table7 = BlocksIO.readMappingTuple("operators/leftJoin/paperTest/table7.json");

        JoinCondition condition = ((s1, s2) ->
                s1.containsKey("?$pet.type") && s1.get("?$pet.type") != null &&
                        s2.containsKey("?type") && s2.get("?alias_type") != null &&
                        s1.get("?$pet.type").equals(s2.get("?alias_type")));

        LeftJoin operator = new LeftJoin("LeftJoin", "f_default", condition, "alias_");

        MappingTuple actual = operator.apply(table6, table7);

        assertEquals(expected, actual);
    }
}

package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FragmentTest {

    // operator with a simple condition that renames the f_default fragment to f_contacts
    FragmenterOperator op = new FragmenterOperator((fragment, mapping) -> {
        MappingTuple tuple = new MappingTuple();
        if (fragment.equals("f_default")) {
            tuple.setSolutionMaps("f_contacts", mapping);
        } else {
            tuple.setSolutionMaps(fragment, mapping);
        }

        return tuple;
    });

    @Test
    public void simpleRename() {
        MappingTuple tuple = BlocksIO.readMappingTuple("operators/fragment/simpleRename/john.json");
        MappingTuple expected = BlocksIO.readMappingTuple("operators/fragment/simpleRename/output.json");
        MappingTuple actual = op.applyMappingTuple(tuple);

        assertEquals(expected, actual);
    }

    @Test
    public void fragmentIntoMultiple() {
        // operator that will fragment f_default into two different fragments based on name
        FragmenterOperator op = new FragmenterOperator(((fragment, mappings) -> {
            MappingTuple out = new MappingTuple();
            if (fragment.equals("f_default")) {
                for (SolutionMapping mapping : mappings) {
                    String newName = String.format("f_%s", mapping.get("?name").getLiteralValue());
                    out.addSolutionMap(newName, mapping);
                }
            } else {
                out.setSolutionMaps(fragment, mappings);
            }

            return out;
        }));

        MappingTuple input = BlocksIO.readMappingTuple("operators/fragment/fragmentMultiple/input.json");

        // apply operator
        MappingTuple actual = op.applyMappingTuple(input);

        MappingTuple expected = BlocksIO.readMappingTuple("operators/fragment/fragmentMultiple/output.json");
        assertEquals(expected, actual);
    }

    @Test
    public void testSolMappingThrowsError() {
        SolutionMapping mapping = new SolutionMapping();

        assertThrows(IllegalStateException.class, () -> {
            op.applySolMapping(mapping);
        });
    }
}

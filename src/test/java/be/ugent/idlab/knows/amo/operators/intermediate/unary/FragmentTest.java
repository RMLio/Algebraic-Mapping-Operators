package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FragmentTest {

    // operator with a simple condition that renames the f_default fragment to f_contacts
    FragmenterOperator op = new FragmenterOperator("FragmentOp", Set.of("f_default"), Set.of("f_default"), (mappingTuple) -> {
        MappingTuple out = new MappingTuple();
        Collection<SolutionMapping> solMappings = mappingTuple.getMap().get("f_default");
        out.setSolutionMaps("f_contacts", solMappings);

        return out;
    });

    @Test
    public void simpleRename() {
        MappingTuple tuple = BlocksIO.readMappingTuple("operators/fragment/simpleRename/john.json");
        MappingTuple expected = BlocksIO.readMappingTuple("operators/fragment/simpleRename/output.json");
        MappingTuple actual = op.apply(tuple);

        assertEquals(expected, actual);
    }

    @Test
    public void fragmentIntoMultiple() {
        // operator that will fragment f_default into two different fragments based on name
        FragmenterOperator op = new FragmenterOperator("FragmentOp", Set.of("f_default"), Set.of("f_default"), ((mappingTuple) -> {
            MappingTuple out = new MappingTuple();

            // perform a fragment of f_default into two fragments
            for (SolutionMapping solmap : mappingTuple.getSolutionMappings("f_default")) {
                String newName = String.format("f_%s", ((LiteralNode)solmap.get("?name")).getValue());
                out.addSolutionMap(newName, solmap);
            }

            // preserve values from other fragments
            for (String fragment : mappingTuple.getFragments()) {
                if (!fragment.equals("f_default")) {
                    out.setSolutionMaps(fragment, mappingTuple.getSolutionMappings(fragment));
                }
            }
            return out;
        }));

        MappingTuple input = BlocksIO.readMappingTuple("operators/fragment/fragmentMultiple/input.json");
        assertEquals(2, input.getSolutionMappings("f_default").size());

        // apply operator
        MappingTuple actual = op.apply(input);

        MappingTuple expected = BlocksIO.readMappingTuple("operators/fragment/fragmentMultiple/output.json");
        assertEquals(expected, actual);
    }

    @Test
    public void testSolMappingThrowsError() {
        SolutionMapping mapping = new SolutionMapping();

        assertThrows(IllegalStateException.class, () -> op.apply(mapping));
    }
}

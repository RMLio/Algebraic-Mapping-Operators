package be.ugent.idlab.knows.amo.operators.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.unary.FragmenterOperator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FragmentTest {

    // operator with a simple condition that renames the f_default fragment to f_contacts
    FragmenterOperator op = new FragmenterOperator((fragment, mapping) -> {
        MappingTuple tuple = new MappingTuple();
        if (fragment.equals("f_default")) {
            tuple.setSolutionMap("f_contacts", mapping);
        } else {
            tuple.setSolutionMap(fragment, mapping);
        }

        return tuple;
    });

    @Test
    public void simpleRename() {
        SolutionMapping mapping = new SolutionMapping(Map.of(
                "?age", 25,
                "?name", "John Smith"
        ));
        MappingTuple tuple = new MappingTuple();
        tuple.addSolutionMap("f_default", mapping);

        MappingTuple output = op.applyMappingTuple(tuple);

        Optional<String> maybeFragmentName = output.getFragments().stream().findFirst();
        assertTrue(maybeFragmentName.isPresent());

        assertEquals(maybeFragmentName.get(), "f_contacts");
    }

    @Test
    public void fragmentIntoMultiple() {
        // operator that will fragment f_default into two different fragments based on name
        FragmenterOperator op = new FragmenterOperator(((fragment, mappings) -> {
            MappingTuple out = new MappingTuple();
            if (fragment.equals("f_default")) {
                for (SolutionMapping mapping : mappings) {
                    String newName = String.format("f_%s", mapping.get("?name"));
                    out.addSolutionMap(newName, mapping);
                }
            } else {
                out.setSolutionMap(fragment, mappings);
            }

            return out;
        }));

        SolutionMapping mappingF1 = new SolutionMapping(Map.of(
                "?name", "John"
        ));

        SolutionMapping mappingF2 = new SolutionMapping(Map.of(
                "?name", "Jane"
        ));

        // both mappings go to the same fragment
        MappingTuple t = new MappingTuple();
        t.addSolutionMap("f_default", mappingF1);
        t.addSolutionMap("f_default", mappingF2);

        // apply operator
        MappingTuple out = op.applyMappingTuple(t);

        assertEquals(2, out.getFragments().size());

        List<String> fragments = out.getFragments().stream().toList();
        assertEquals("f_John", fragments.get(0));
        assertEquals("f_Jane", fragments.get(1));

        // verify that the fragment do not share solution mappings
        SolutionMapping solJohn = out.getSolutionMappings("f_John").stream().toList().get(0);
        assertEquals(1, solJohn.size());
        assertEquals("John", solJohn.get("?name"));

        SolutionMapping solJane = out.getSolutionMappings("f_Jane").stream().toList().get(0);
        assertEquals(1, solJane.size());
        assertEquals("Jane", solJane.get("?name"));
    }

    @Test
    public void testSolMappingThrowsError() {
        SolutionMapping mapping = new SolutionMapping(Map.of(
                "?age", 25,
                "?name", "John Smith"
        ));

        assertThrows(IllegalStateException.class, () -> {
            op.applySolMapping(mapping);
        });
    }
}

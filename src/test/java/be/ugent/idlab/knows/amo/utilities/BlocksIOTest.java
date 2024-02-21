package be.ugent.idlab.knows.amo.utilities;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BlocksIOTest {

    @Nested
    class SolutionMapReadingTests {
        /**
         * A simple smoke test for reading a very simple solution map
         */
        @Test
        public void testReadSimpleSolutionMap() {
            String filepath = "src/test/resources/file_reading_tests/solution_map/simple_sol_map.json";
            SolutionMapping sm = BlocksIO.readSolutionMapping(filepath);

            assertEquals(2, sm.keySet().size());
            assertEquals("bar", sm.get("?foo"));
            assertEquals(0, sm.get("?baz"));
        }

        /**
         * Test verifying that nested objects are not allowed
         */
        @Test
        public void testErrorNestedMap() {
            String filepath = "src/test/resources/file_reading_tests/solution_map/nested_map.json";
            assertThrows(IllegalArgumentException.class, () -> BlocksIO.readSolutionMapping(filepath));
        }
    }

    @Nested
    class MappingTupleReadingTests {
        /**
         * Smoke test for reading a MappingTuple from a file
         */
        @Test
        public void testReadSimpleMappingTuple() {
            String filepath = "src/test/resources/file_reading_tests/mapping_tuple/simple_mapping_tuple.json";
            MappingTuple mt = BlocksIO.readMappingTuple(filepath);

            assertEquals(1, mt.getFragments().size());
            assertEquals("f_default", mt.getFragments().stream().toList().get(0));

            Collection<SolutionMapping> sms = mt.getSolutionMappings("f_default");
            assertEquals(1, sms.size());
            SolutionMapping sm = sms.stream().toList().get(0);
            assertEquals(2, sm.size());
            assertEquals("bar", sm.get("?foo"));
            assertEquals(0, sm.get("?baz"));
        }

        /**
         * Verifies that an exception is thrown when confronted with empty file
         */
        @Test
        public void testEmpty() {
            String filepath = "src/test/resources/file_reading_tests/mapping_tuple/empty.json";
            assertThrows(IllegalArgumentException.class, () -> BlocksIO.readMappingTuple(filepath));
        }

        @Test
        public void testEmptyMT() {
            String filepath = "src/test/resources/file_reading_tests/mapping_tuple/emptyMT.json";
            MappingTuple mt = BlocksIO.readMappingTuple(filepath);
            assertEquals(0, mt.getFragments().size());
        }

        /**
         * Test to verify acceptance of multiple fragments
         */
        @Test
        public void testMultipleFragments() {
            String filepath = "src/test/resources/file_reading_tests/mapping_tuple/mapping_tuple_multiple_fragments.json";
            MappingTuple mt = BlocksIO.readMappingTuple(filepath);

            assertEquals(2, mt.getFragments().size());
            List<String> fragments = mt.getFragments().stream().toList();
            assertEquals("f_default", fragments.get(0));
            assertEquals("f_contacts", fragments.get(1));

            List<SolutionMapping> f_defaultSMs = mt.getSolutionMappings("f_default").stream().toList();
            assertEquals(1, f_defaultSMs.size());
            SolutionMapping f_defaultSM = f_defaultSMs.get(0);
            assertEquals(2, f_defaultSM.size());
            assertEquals("bar", f_defaultSM.get("?foo"));
            assertEquals(0, f_defaultSM.get("?baz"));

            List<SolutionMapping> f_contactsSMs = mt.getSolutionMappings("f_contacts").stream().toList();
            assertEquals(1, f_contactsSMs.size());
            SolutionMapping f_contactsSM = f_contactsSMs.get(0);
            assertEquals(1, f_contactsSM.size());
            assertEquals("John", f_contactsSM.get("?name"));
        }

        /**
         * Test to verify acceptance of a list of SolutionMappings for a particular fragment
         */
        @Test
        public void testSMList() {
            String filepath = "src/test/resources/file_reading_tests/mapping_tuple/mt_list.json";
            MappingTuple mt = BlocksIO.readMappingTuple(filepath);

            assertEquals(1, mt.getFragments().size());
            assertEquals("f_default", mt.getFragments().stream().toList().get(0));

            List<SolutionMapping> sms = mt.getSolutionMappings("f_default").stream().toList();
            SolutionMapping sm1 = sms.get(0);
            assertEquals(1, sm1.size());
            assertEquals("bar", sm1.get("?foo"));

            SolutionMapping sm2 = sms.get(1);
            assertEquals(1, sm2.size());
            assertEquals(0, sm2.get("?baz"));
        }

        /**
         * Verifies the rejection of a nested SolutionMapping
         */
        @Test
        public void testRejectNestedSolMap() {
            String filepath = "src/test/resources/file_reading_tests/mapping_tuple/mt_nested_solmap.json";
            assertThrows(IllegalArgumentException.class, () -> BlocksIO.readMappingTuple(filepath));
        }
    }
}

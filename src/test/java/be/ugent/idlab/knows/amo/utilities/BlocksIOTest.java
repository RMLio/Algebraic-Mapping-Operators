package be.ugent.idlab.knows.amo.utilities;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.CollectionNode;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BlocksIOTest {

    @Nested
    class SolutionMapReadingTests {

        /**
         * A simple smoke test for reading a very simple solution map
         */
        @Test
        public void testReadSolMap() {
            String filepath = "serialization/solution_map/solMap.json";
            SolutionMapping sm = BlocksIO.readSolutionMapping(filepath);

            assertEquals(6, sm.keySet().size());

            assertTrue(sm.get("?literalString").isLiteral());
            assertEquals("foo", sm.get("?literalString").getValue());

            assertTrue(sm.get("?number").isLiteral());
            assertEquals(0, ((LiteralNode)sm.get("?number")).getValueObject());

            assertTrue(sm.get("?blank").isBlank());
            assertEquals("blankLabel", sm.get("?blank").getValue());

            assertTrue(sm.get("?iri").isIRI());
            assertEquals("http://example.com", sm.get("?iri").getValue());

            assertTrue(sm.get("?null").isNull());

            // a collection's members are terms themselves, of any type
            assertTrue(sm.get("?collection").isCollection());
            List<RDFNode> members = ((CollectionNode) sm.get("?collection")).members();
            assertEquals(2, members.size());
            assertTrue(members.get(0).isLiteral());
            assertEquals("read", members.get(0).getValue());
            assertTrue(members.get(1).isIRI());
            assertEquals("http://example.com/write", members.get(1).getValue());
        }

        @Nested
        class Errors {
            @Test
            public void testMissingType() {
                String filepath = "serialization/solution_map/errors/missingType.json";
                assertThrows(IllegalArgumentException.class, () -> BlocksIO.readSolutionMapping(filepath));
            }

            @Test
            public void testMissingValue() {
                String filepath = "serialization/solution_map/errors/missingValue.json";
                assertThrows(IllegalArgumentException.class, () -> BlocksIO.readSolutionMapping(filepath));
            }

            @Test
            public void testBadType() {
                String filepath = "serialization/solution_map/errors/badType.json";
                assertThrows(IllegalArgumentException.class, () -> BlocksIO.readSolutionMapping(filepath));
            }

            @Test
            public void testBadDataType() {
                String filepath = "serialization/solution_map/errors/badDataType.json";
                assertThrows(IllegalArgumentException.class, () -> BlocksIO.readSolutionMapping(filepath));
            }
        }
    }

    @Nested
    class MappingTupleReadingTests {
        /**
         * Smoke test for reading a MappingTuple from a file
         */
        @Test
        public void testReadSimpleMappingTuple() {
            String filepath = "serialization/mapping_tuple/simple_mapping_tuple.json";
            MappingTuple mt = BlocksIO.readMappingTuple(filepath);

            assertEquals(1, mt.getFragments().size());
            assertEquals("f_default", mt.getFragments().stream().toList().get(0));

            List<SolutionMapping> sms = mt.getSolutionMappings("f_default").stream().toList();
            assertEquals(1, sms.size());
            SolutionMapping sm = sms.get(0);
            assertEquals(2, sm.size());
            assertEquals("bar", ((LiteralNode)sm.get("?foo")).getValue());
            assertEquals(0, ((LiteralNode)sm.get("?baz")).getValueObject());
        }

        /**
         * Verifies that an exception is thrown when confronted with empty file
         */
        @Test
        public void testEmpty() {
            String filepath = "serialization/mapping_tuple/empty.json";
            assertThrows(IllegalArgumentException.class, () -> BlocksIO.readMappingTuple(filepath));
        }

        @Test
        public void testEmptyMT() {
            String filepath = "serialization/mapping_tuple/emptyMT.json";
            MappingTuple mt = BlocksIO.readMappingTuple(filepath);
            assertEquals(0, mt.getFragments().size());
        }

        /**
         * Test to verify acceptance of multiple fragments
         */
        @Test
        public void testMultipleFragments() {
            String filepath = "serialization/mapping_tuple/mapping_tuple_multiple_fragments.json";
            MappingTuple mt = BlocksIO.readMappingTuple(filepath);

            assertEquals(2, mt.getFragments().size());
            List<String> fragments = mt.getFragments().stream().toList();
            assertTrue(fragments.contains("f_default"));
            assertTrue(fragments.contains("f_contacts"));

            List<SolutionMapping> f_defaultSMs = mt.getSolutionMappings("f_default").stream().toList();
            assertEquals(1, f_defaultSMs.size());
            SolutionMapping f_defaultSM = f_defaultSMs.get(0);
            assertEquals(2, f_defaultSM.size());
            assertEquals("bar",  f_defaultSM.get("?foo").getValue());
            assertEquals(0, ((LiteralNode)f_defaultSM.get("?baz")).getValueObject());

            List<SolutionMapping> f_contactsSMs = mt.getSolutionMappings("f_contacts").stream().toList();
            assertEquals(1, f_contactsSMs.size());
            SolutionMapping f_contactsSM = f_contactsSMs.get(0);
            assertEquals(1, f_contactsSM.size());
            assertEquals("John", f_contactsSM.get("?name").getValue());
        }

        /**
         * Test to verify acceptance of a list of SolutionMappings for a particular fragment
         */
        @Test
        public void testSMList() {
            String filepath = "serialization/mapping_tuple/mt_list.json";
            MappingTuple mt = BlocksIO.readMappingTuple(filepath);

            assertEquals(1, mt.getFragments().size());
            assertEquals("f_default", mt.getFragments().stream().toList().get(0));

            Collection<SolutionMapping> sms = mt.getSolutionMappings("f_default");
            SolutionMapping sm1 = sms.stream().filter(m -> m.containsKey("?foo")).findFirst().get();
            SolutionMapping sm2 = sms.stream().filter(m -> m.containsKey("?baz")).findFirst().get();

            // decide which one
            assertEquals(1, sm1.size());
            assertEquals("bar", sm1.get("?foo").getValue());

            assertEquals(1, sm2.size());
            assertEquals(0, ((LiteralNode)sm2.get("?baz")).getValueObject());
        }

        /**
         * Verifies the rejection of a nested SolutionMapping
         */
        @Test
        public void testRejectNestedSolMap() {
            String filepath = "serialization/mapping_tuple/mt_nested_solmap.json";
            assertThrows(IllegalArgumentException.class, () -> BlocksIO.readMappingTuple(filepath));
        }
    }
}

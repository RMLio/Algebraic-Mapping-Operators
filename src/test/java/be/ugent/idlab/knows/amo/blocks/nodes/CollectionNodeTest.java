package be.ugent.idlab.knows.amo.blocks.nodes;

/**
 * Several terms held as one value.
 */
public class CollectionNodeTest {

//    private static CollectionNode of(String... values) {
//        return new CollectionNode(List.of(values).stream().map(v -> (RDFNode) new LiteralNode(v)).toList());
//    }
//
//    @Test
//    public void itHoldsItsMembersInOrder() {
//        CollectionNode collection = of("read", "write");
//
//        assertEquals(2, collection.members().size());
//        assertEquals("read", collection.members().get(0).getValue().toString());
//        assertEquals("write", collection.members().get(1).getValue().toString());
//    }
//
//    @Test
//    public void itIsACollectionAndOtherNodesAreNot() {
//        assertTrue(of("read").isCollection());
//        assertFalse(new LiteralNode("read").isCollection());
//        assertFalse(new IRINode("http://example.com/read").isCollection());
//        assertFalse(new NullNode().isCollection());
//    }
//
//    @Test
//    public void aCollectionCanBeEmpty() {
//        CollectionNode empty = new CollectionNode(List.of());
//
//        assertTrue(empty.members().isEmpty());
//        assertTrue(empty.isCollection());
//    }
//
//    @Test
//    public void aCollectionIsNotATerm() {
//        // whoever holds a collection has to generate a term per member; there is no single
//        // term to fall back on
//        assertThrows(UnsupportedOperationException.class, () -> of("read", "write").getJenaNode());
//    }
//
//    @Test
//    public void collectionsWithTheSameMembersAreEqual() {
//        assertEquals(of("read", "write"), of("read", "write"));
//    }
}

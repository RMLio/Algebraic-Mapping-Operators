package be.ugent.idlab.knows.amo.blocks;

import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeTests {

    /**
     * Tests the formating of Literal nodes such that double values are correctly formatted in scientific notation
     */
    @Test
    public void formatScientific() {
        LiteralNode node = new LiteralNode("30.0", XSDDatatype.XSDdouble);

        String expected = "\"3.0E1\"^^<http://www.w3.org/2001/XMLSchema#double>";
        assertEquals(expected, node.getStringRepr());
    }
}

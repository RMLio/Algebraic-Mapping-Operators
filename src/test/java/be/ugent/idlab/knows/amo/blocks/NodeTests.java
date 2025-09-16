package be.ugent.idlab.knows.amo.blocks;

import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeTests {

    /**
     * Tests the formating of Literal nodes such that double values are correctly formatted in scientific notation
     */
    @Test
    public void formatScientific() {
        LiteralNode node = new LiteralNode("30.0", XSDDatatype.XSDdouble);

        String expected = "\"3.0E1\"^^<http://www.w3.org/2001/XMLSchema#double>";
        assertEquals(expected, node.toString());
    }

    @Test
    public void customDatatype() {
        LiteralNode node = new LiteralNode("2", "http://example.com/base/datatype#int");

        String expected = "\"2\"^^<http://example.com/base/datatype#int>";

        assertEquals(expected, node.toString());
    }

//    static {
//        Configuration.setDefaults(new Configuration.Defaults() {
//
//            private final JsonProvider jsonProvider = Configuration.defaultConfiguration().jsonProvider();
//            private final MappingProvider mappingProvider = Configuration.defaultConfiguration().mappingProvider();
//
//            @Override
//            public JsonProvider jsonProvider() {
//                return jsonProvider;
//            }
//
//            @Override
//            public Set<Option> options() {
//                return Set.of(Option.DEFAULT_PATH_LEAF_TO_NULL, Option.REQUIRE_PROPERTIES, Option.ALWAYS_RETURN_LIST);
//            }
//
//            @Override
//            public MappingProvider mappingProvider() {
//                return mappingProvider;
//            }
//        });
//    }

    @Test
    public void foo() {
        String json = "{\"foo\": [10,20,30], \"bar\": 1}";

        System.out.println(json);
    }
}

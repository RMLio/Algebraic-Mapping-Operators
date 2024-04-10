package be.ugent.idlab.knows.amo.utilities;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

@FunctionalInterface
interface ParsingFunction {
    Node parse(JSONObject json);
}

/**
 * Class responsible for reading in SolutionMappings and MappingTuples from json files
 */
public class BlocksIO {
    private static final Map<String, ParsingFunction> PARSING_FUNCTIONS = Map.of(
            "iri", BlocksIO::parseIRI,
            "blank", BlocksIO::parseBlank,
            "literal", BlocksIO::parseLiteral
    );

    private static final Set<String> ALLOWED_TYPES = Set.of("iri", "blank", "literal");

    /**
     *
     * @param filepath filepath with respect to src/test/resources/
     * @return
     */
    private static JSONObject readJSON(String filepath) {
        try (InputStream is = new FileInputStream("src/test/resources/" + filepath)) {
            byte[] bytes = is.readAllBytes();
            if (bytes.length == 0) {
                throw new IllegalArgumentException(String.format("File %s is empty!", filepath));
            }
            return new JSONObject(new String(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for reading
     * @param filepath
     * @return
     */
    public static SolutionMapping readSolutionMapping(String filepath) {
        JSONObject json = readJSON(filepath);
        return solMapFromJSON(json);
    }

    private static SolutionMapping solMapFromJSON(JSONObject jsonObject) {
        SolutionMapping map = new SolutionMapping();
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (!(value instanceof JSONObject)) {
                throw new IllegalArgumentException(String.format("Unexpected value %s for key %s in %s, expected a JSON object", value, key, jsonObject));
            }

            Node term = parseTerm((JSONObject) value);

            map.put(key, term);
        }
        return map;
    }

    private static Node parseTerm(JSONObject json) {
        if (!json.has("value")) {
            throw new IllegalArgumentException(String.format("%s is missing a required field 'value'!", json));
        }

        if (!json.has("type")) {
            throw new IllegalArgumentException(String.format("%s is missing a required field 'type'!", json));
        }

        String type = json.getString("type");
        if (!ALLOWED_TYPES.contains(type)) {
            throw new IllegalArgumentException(String.format("Unexpected type '%s' for object '%s', expected one of %s", type, json,  ALLOWED_TYPES));
        }

        return PARSING_FUNCTIONS.get(type).parse(json);
    }

    public static MappingTuple readMappingTuple(String filepath) {
        JSONObject json = readJSON(filepath);
        MappingTuple mappingTuple = new MappingTuple();
        for (String key : json.keySet()) {
            Object next = json.get(key);
            if (next instanceof JSONObject jsonObject) { // must be a SolutionMapping
                mappingTuple.addSolutionMap(key, solMapFromJSON(jsonObject));
            } else if (next instanceof JSONArray jsonArray) {
                for (Object obj : jsonArray) {
                    // all these objects now must be SolutionMappings => JSONObjects
                    if (obj instanceof JSONObject jsonObject) {
                        mappingTuple.addSolutionMap(key, solMapFromJSON(jsonObject));
                    }
                }
            }
        }

        return mappingTuple;
    }

    private static Node parseIRI(JSONObject json) {
        return NodeFactory.createURI(json.getString("value"));
    }

    private static Node parseLiteral(JSONObject json) {
        if (!json.has("datatype") || json.getString("datatype").equals("string")) {
            if (json.has("language")) {
                return NodeFactory.createLiteral(json.getString("value"), json.getString("language"));
            }
            return NodeFactory.createLiteral(json.getString("value"), XSDDatatype.XSDstring);
        }

        String datatype = json.getString("datatype");
        try {
            XSDDatatype dt = new XSDDatatype(datatype);
            return NodeFactory.createLiteral(json.getString("value"), dt);
        } catch (NullPointerException npe) {
            throw new IllegalArgumentException(String.format("Unsupported datatype %s found in %s", datatype, json));
        }
    }

    private static Node parseBlank(JSONObject json) {
        return NodeFactory.createBlankNode(json.getString("value"));
    }
}

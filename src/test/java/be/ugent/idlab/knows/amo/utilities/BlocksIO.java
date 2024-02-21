package be.ugent.idlab.knows.amo.utilities;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class responsible for reading in SolutionMappings and MappingTuples from json files
 */
public class BlocksIO {

    private static JSONObject readJSON(String filepath) {
        try (InputStream is = new FileInputStream(filepath)) {
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
            if (value instanceof JSONObject || value instanceof JSONArray) {
                throw new IllegalArgumentException(String.format("Value %s for key %s is illegal!", value, key));
            }

            map.put(key, value);
        }
        return map;
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
}

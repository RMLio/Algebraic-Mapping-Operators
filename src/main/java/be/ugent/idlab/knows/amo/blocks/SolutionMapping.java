package be.ugent.idlab.knows.amo.blocks;

import java.util.HashMap;
import java.util.Map;

/**
 * Solution mapping is a collection of key-value pairs connecting variables to their data
 * These can be applied on a templated string
 */
public class SolutionMapping extends HashMap<String, Object> {

    public SolutionMapping() {

    }

    public SolutionMapping(Map<String, Object> variables) {
        this.putAll(variables);
    }

    public String apply(String templateString) {
        for (Map.Entry<String, Object> entry : this.entrySet()) {
            if (templateString.contains(entry.getKey())) {
                templateString = templateString.replace(entry.getKey(), entry.getValue().toString());
            }
        }

        return templateString;
    }
}

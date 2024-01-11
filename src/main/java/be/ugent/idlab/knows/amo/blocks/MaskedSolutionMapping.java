package be.ugent.idlab.knows.amo.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaskedSolutionMapping extends SolutionMapping {

    private List<String> maskedVariables;

    public MaskedSolutionMapping() {
        super();
        this.maskedVariables = new ArrayList<>();
    }

    public MaskedSolutionMapping(Map<String, Object> variables) {
        super(variables);
    }

    public void maskVariables(List<String> mask) {
        this.maskedVariables = mask;
    }


    @Override
    public String apply(String templateString) {
        for(Map.Entry<String, Object> entry : variables.entrySet()) {
            if (this.maskedVariables.contains(entry.getKey()) && templateString.contains(entry.getKey())) {
                templateString = templateString.replace(entry.getKey(), entry.getValue().toString());
            }
        }

        return templateString;
    }
}

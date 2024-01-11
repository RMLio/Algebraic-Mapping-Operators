package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

import java.util.List;
import java.util.Map;

/**
 * Project operator responsible for restricting the solution mappings.
 */
public class ProjectOperator {

    public SolutionMapping apply(SolutionMapping mapping, List<String> variables) {
        Map<String, Object> mappingVars = mapping.getVariables();
        for (String variable : variables) {
            mappingVars.remove(variable);
        }

        return mapping;
    }
}

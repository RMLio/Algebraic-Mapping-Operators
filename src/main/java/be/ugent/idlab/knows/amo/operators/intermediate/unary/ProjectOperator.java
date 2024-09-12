package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;

import java.util.Collection;
import java.util.Map;

import org.jspecify.annotations.Nullable;

/**
 * Project operator responsible for restricting the solution mappings.
 * All variables that are not contained in the supplied collection will be
 * removed from the SolutionMapping / MappingTuple.
 */
public class ProjectOperator extends UnaryOperator {

    private final Collection<String> variables;

    public ProjectOperator(String operatorName, String fragment, Collection<String> variables) {
        super(operatorName, fragment);
        this.variables = variables;
    }

    @Override
    @Nullable
    public SolutionMapping apply(@Nullable SolutionMapping mapping) {
        if (mapping == null) {
            return null;
        }
        SolutionMapping newMapping = new SolutionMapping();
        for (Map.Entry<String, RDFNode> entry : mapping.entrySet()) {
            if (this.variables.contains(entry.getKey())) {
                newMapping.put(entry.getKey(), entry.getValue());
            }
        }

        return newMapping;
    }
}

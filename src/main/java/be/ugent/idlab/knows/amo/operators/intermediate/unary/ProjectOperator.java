package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;

/**
 * Project operator responsible for restricting the solution mappings.
 * All variables that are not contained in the supplied collection will be
 * removed from the SolutionMapping / MappingTuple.
 */
public class ProjectOperator extends UnaryOperator {

    private final Collection<String> variables;

    /**
     * Creates a new instance of a ProjectOperator
     * @param operatorName A name (identifier) for the operator.
     * @param inputFragments    The input fragments of the operator.
     * @param outputFragments   The output fragments of the operator.
     * @param variables         The variables to keep in the solution mapping when applying this operator.
     */
    public ProjectOperator(String operatorName, Set<String> inputFragments, Set<String> outputFragments, Collection<String> variables) {
        super(operatorName, inputFragments, outputFragments);
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

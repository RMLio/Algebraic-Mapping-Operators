package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.OperatorVisitor;
import org.apache.jena.graph.Node;

import java.util.Collection;
import java.util.Map;

/**
 * Project operator responsible for restricting the solution mappings.
 * All variables that are not contained in the supplied collection will be removed from the SolutionMapping / MappingTuple.
 */
public class ProjectOperator implements UnaryOperator {

    private final Collection<String> variables;

    public ProjectOperator(Collection<String> variables) {
        this.variables = variables;
    }

    @Override
    public <T> T accept(OperatorVisitor<T> visitor) {
        return visitor.visitProject(this);
    }

    public SolutionMapping apply(SolutionMapping mapping) {
        SolutionMapping newMapping = new SolutionMapping(mapping);
        for (Map.Entry<String, Node> entry : mapping.entrySet()) {
            if (!this.variables.contains(entry.getKey())) {
                newMapping.put(entry.getKey(), entry.getValue());
            }
        }

        return mapping;
    }
}

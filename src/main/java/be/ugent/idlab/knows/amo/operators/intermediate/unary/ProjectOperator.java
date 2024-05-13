package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import org.apache.jena.graph.Node;

import java.io.ObjectInputStream;
import java.io.Serial;
import java.util.Collection;
import java.util.Map;

/**
 * Project operator responsible for restricting the solution mappings.
 * All variables that are not contained in the supplied collection will be removed from the SolutionMapping / MappingTuple.
 */
public class ProjectOperator implements UnaryOperator {

    private Collection<String> variables;

    public ProjectOperator(Collection<String> variables) {
        this.variables = variables;
    }

    @Serial
    private void readObject(ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
        this.bootstrap();
    }

    private void bootstrap() {

    }

    public SolutionMapping apply(SolutionMapping mapping) {
        SolutionMapping newMapping = new SolutionMapping();
        for (Map.Entry<String, RDFNode> entry : mapping.entrySet()) {
            if (this.variables.contains(entry.getKey())) {
                newMapping.put(entry.getKey(), entry.getValue());
            }
        }

        return newMapping;
    }
}

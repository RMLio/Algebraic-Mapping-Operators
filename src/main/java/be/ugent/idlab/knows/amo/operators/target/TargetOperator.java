package be.ugent.idlab.knows.amo.operators.target;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.functions.TargetSink;
import be.ugent.idlab.knows.amo.operators.Operator;
import be.ugent.idlab.knows.amo.operators.OperatorVisitor;

import java.io.Serializable;
import java.util.Collection;

/**
 * TargetOperator will perform side effects on the MappingTuple.
 * This is the output of the mapping plan, writing the results into a file or standard output or ... according to the TargetSink function
 */
public class TargetOperator implements Operator, Serializable {

    private final String targetFragment;
    private final String targetVariable;
    private final TargetSink<RDFNode> sink;

    /**
     * @param targetFragment fragment to write
     * @param sink           sink to serialize the fragment into
     */
    public TargetOperator(String targetFragment, String targetVariable, TargetSink<RDFNode> sink) {
        this.targetFragment = targetFragment;
        this.targetVariable = targetVariable;
        this.sink = sink;
    }

    public void apply(MappingTuple mappingTuple) {
        Collection<SolutionMapping> solMappings = mappingTuple.getSolutionMappings(this.targetFragment);
        for (SolutionMapping solMapping : solMappings) {
            sink.sink(solMapping.get(targetVariable));
        }
    }

    @Override
    public <T> T visit(OperatorVisitor<T> visitor) {
        return visitor.visitTarget(this);
    }
}

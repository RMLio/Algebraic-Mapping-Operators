package be.ugent.idlab.knows.amo.operators.target;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.functions.TargetSink;
import be.ugent.idlab.knows.amo.operators.Operator;
import be.ugent.idlab.knows.amo.operators.OperatorVisitor;

import java.util.Collection;

import org.jspecify.annotations.NonNull;

/**
 * TargetOperator will perform side effects on the MappingTuple.
 * This is the output of the mapping plan, writing the results into a file or
 * standard output or ... according to the TargetSink function
 */
public class TargetOperator extends Operator {

    public static final String TARGET_VARIABLE = "?serialized_output";

    private final String targetFragment;
    private final String targetVariable;
    private final TargetSink<RDFNode> sink;

    public TargetOperator(String operatorName, String targetFragment, String targetVariable, TargetSink<RDFNode> sink) {
        super(operatorName);
        this.targetFragment = targetFragment;
        this.targetVariable = targetVariable;
        this.sink = sink;
    }

    public String getTargetFragment() {
        return this.targetFragment;
    }

    public String getTargetVariable() {
        return this.targetVariable;
    }

    public TargetSink<RDFNode> getSink() {
        return this.sink;
    }

    public void apply(MappingTuple mappingTuple) {
        Collection<SolutionMapping> solMappings = mappingTuple.getSolutionMappings(this.targetFragment);
        for (SolutionMapping solMapping : solMappings) {
            this.sink.sink(solMapping.get(this.targetVariable));
        }
    }

    @Override
    @NonNull
    public <@NonNull T> T accept(@NonNull OperatorVisitor<T> visitor) {
        return visitor.visitTarget(this);
    }
}

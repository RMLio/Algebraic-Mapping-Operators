package be.ugent.idlab.knows.amo.operators.target;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.functions.TargetSink;
import be.ugent.idlab.knows.amo.operators.Operator;
import be.ugent.idlab.knows.amo.operators.OperatorVisitor;
import be.ugent.idlab.knows.amo.operators.target.postprocessing.RDFFormatter;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * TargetOperator will perform side effects on the MappingTuple.
 * This is the output of the mapping plan, writing the results into a file or
 * standard output or ... according to the TargetSink function
 */
public class TargetOperator extends Operator {

    public static final String TARGET_VARIABLE = "?serialized_output";

    private final String targetVariable;
    private final TargetSink<RDFNode> sink;
    private final RDFFormatter formatter;

    public TargetOperator(String operatorName, Set<String> inputFragments, String targetVariable, TargetSink<RDFNode> sink) {
        this(operatorName, inputFragments, targetVariable, sink, null);
    }

    public TargetOperator(String operatorName, Set<String> inputFragments, String targetVariable, TargetSink<RDFNode> sink, RDFFormatter formatter) {
        super(operatorName, inputFragments, Set.of());
        this.targetVariable = targetVariable;
        this.sink = sink;
        this.formatter = formatter;
    }

    public String getTargetVariable() {
        return this.targetVariable;
    }

    public TargetSink<RDFNode> getSink() {
        return this.sink;
    }

    public void apply(MappingTuple mappingTuple) {
        Collection<SolutionMapping> solMappings = new ArrayList<>();
        for (String inputFragment : getInputFragments()) {
            solMappings.addAll(mappingTuple.getSolutionMappings(inputFragment));
        }

        for (SolutionMapping solMapping : solMappings) {
            LiteralNode node = (LiteralNode) solMapping.get(this.targetVariable);
            if (node == null) {
                throw new IllegalStateException("Target node " + this.targetVariable + " not found");
            }
            if (this.formatter != null) {
                String value = this.formatter.from(node.toString()).output();
                node = new LiteralNode(value, node.getDatatype(), node.getLanguage());
            }
            this.sink.sink(node);
        }
    }

    @Override
    @NonNull
    public <@NonNull T> T accept(@NonNull OperatorVisitor<T> visitor) {
        return visitor.visitTarget(this);
    }
}

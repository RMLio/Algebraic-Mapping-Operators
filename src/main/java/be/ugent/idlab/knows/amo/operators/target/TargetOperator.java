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
    private final TargetSink<String> sink;
    private final RDFFormatter formatter;

    public TargetOperator(String operatorName, Set<String> inputFragments, String targetVariable, TargetSink<String> sink) {
        this(operatorName, inputFragments, targetVariable, sink, null);
    }

    public TargetOperator(String operatorName, Set<String> inputFragments, String targetVariable, TargetSink<String> sink, RDFFormatter formatter) {
        super(operatorName, inputFragments, Set.of());
        this.targetVariable = targetVariable;
        this.sink = sink;
        this.formatter = formatter;
    }

    public String getTargetVariable() {
        return this.targetVariable;
    }

    public TargetSink<String> getSink() {
        return this.sink;
    }

    public void apply(MappingTuple mappingTuple) {
        Collection<SolutionMapping> solMappings = new ArrayList<>();
        for (String inputFragment : getInputFragments()) {
            solMappings.addAll(mappingTuple.getSolutionMappings(inputFragment));
        }

        for (SolutionMapping solMapping : solMappings) {
            if (solMapping != null) {
                RDFNode solutionNode = solMapping.get(this.targetVariable);
                if (solutionNode != null && !solutionNode.isNull()) {
                    LiteralNode node = (LiteralNode) solutionNode;
                    String serializedOutput = node.getValue();    // The literal node only contains the serialized output from de Serialize operator, so we need the lexical form.

                    // (re)format output
                    if (this.formatter != null) {   // TODO: is this necessary? The serializer operator also formats the output, right?
                        serializedOutput = this.formatter.from(serializedOutput).output();
                    }
                    this.sink.sink(serializedOutput);
                }
            }
        }
    }

    @Override
    @NonNull
    public <@NonNull T> T accept(@NonNull OperatorVisitor<T> visitor) {
        return visitor.visitTarget(this);
    }
}

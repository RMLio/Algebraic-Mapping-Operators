package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.TargetSink;
import org.apache.jena.graph.Node_Literal;

import java.util.Collection;
import java.util.List;

/**
 * TargetOperator will perform side effects on the MappingTuple.
 * This is the output of the mapping plan, writing the results into a file or standard output or ... according to the TargetSink function
 */
public class TargetOperator implements Operator {

    private final String targetFragment;
    private final String targetVariable;
    private final TargetSink<Node_Literal> sink;

    /**
     *
     * @param targetFragment fragment to write
     * @param sink sinnk to serialize the fragment into
     */
    public TargetOperator(String targetFragment, String targetVariable, TargetSink<Node_Literal> sink) {
        this.targetFragment = targetFragment;
        this.targetVariable = targetVariable;
        this.sink = sink;
    }

    public void apply(MappingTuple mappingTuple) {
        Collection<SolutionMapping> solMappings = mappingTuple.getSolutionMappings(this.targetFragment);
        for (SolutionMapping solMapping : solMappings) {
            sink.sink((Node_Literal) solMapping.get(targetVariable));
        }
    }

    /**
     * @param tuples tuples to process
     * @return empty list, because this is a terminator operator
     */
    @Override
    public Collection<MappingTuple> apply(Collection<MappingTuple> tuples) {
        for (MappingTuple tuple : tuples) {
            apply(tuple);
        }
        return List.of();
    }
}

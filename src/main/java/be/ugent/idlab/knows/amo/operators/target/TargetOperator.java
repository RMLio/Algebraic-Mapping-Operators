package be.ugent.idlab.knows.amo.operators.target;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.functions.TargetSink;
import org.apache.jena.graph.Node_Literal;

import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

/**
 * TargetOperator will perform side effects on the MappingTuple.
 * This is the output of the mapping plan, writing the results into a file or standard output or ... according to the TargetSink function
 */
public class TargetOperator implements Serializable {

    private  String targetFragment;
    private  String targetVariable;
    private  TargetSink<RDFNode> sink;

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

    @Serial
    private void readObject(ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
        this.bootstrap();
    }

    private void bootstrap() {

    }
}

package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.BGP;
import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import org.apache.jena.riot.*;
import org.apache.jena.sparql.core.DatasetGraph;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serial;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Serialize operator will accept a Basic Graph Pattern and replace the
 * variables with the values as provided in the mapping tuple.
 */
public class SerializeOperator extends UnaryOperator {

    private final BGP bgp;
    private final Lang language;

    /**
     * Creates a new instance of a SerializeOperator.
     * @param operatorName      A name (identifier) for the operator.
     * @param inputFragments    The input fragments of the operator.
     * @param outputFragments   The output fragments of the operator.
     * @param bgp               The basic graph pattern to apply on the variables.
     * @param language          The RDF serialization format.
     */
    public SerializeOperator(String operatorName, Set<String> inputFragments, Set<String> outputFragments, BGP bgp, String language) {
        super(operatorName, inputFragments, outputFragments);
        this.bgp = bgp;
        this.language = RDFLanguages.nameToLang(language);
    }

    @Serial
    private void readObject(ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
    }

    @Override
    @Nullable
    public SolutionMapping apply(@Nullable SolutionMapping mapping) {
        throw new IllegalStateException("Serialize operator is undefined for SolutionMappings");
    }

    /**
     * Serializes MappingTuple into a graph as described by the BGP, with variables
     * replaced
     *
     * @param m        MappingTuple to serialize
     * @return a MappingTuple with the serialization contained in the variable
     *         "?serialized_output"
     */

    @Override
    @Nullable
    public MappingTuple apply(@Nullable MappingTuple m) {
        if (m == null) {
            return null;
        }
        if (m.getMap().keys().isEmpty()) {
            return m;
        }

        MappingTuple out = new MappingTuple();

        for (String inputFragment : getInputFragments()) {
            Collection<SolutionMapping> solMappings = m.getSolutionMappings(inputFragment);
            for (SolutionMapping solMapping : solMappings) {
                DatasetGraph graph = this.bgp.apply(solMapping);

                // TODO: this graph should be put out as a String, NOT the graph wrapped in a solution mapping

                OutputStream outputStream = new ByteArrayOutputStream();

                RDFWriter.source(graph)
                        .lang(language)
                        .output(outputStream);

                // Jena will prepend labels of Blank nodes with a 'B', which is not what we want
                String serialized = outputStream.toString().replace("_:B", "_:");

                // TODO: Here a LiteralNode gets abused to store the serialized output of this operator as a String.
                //       The solution mapping fragment -> RDFNode doesn't make sense here, so this must be refactored.
                SolutionMapping solMapOut = new SolutionMapping(
                        Map.of("?serialized_output", new LiteralNode(serialized)));

                for (String outputFragment : getOutputFragments()) {
                    out.addSolutionMap(outputFragment, solMapOut);
                }
            }
        }

        return out;
    }
}

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

/**
 * Serialize operator will accept a Basic Graph Pattern and replace the
 * variables with the values as provided in the mapping tuple.
 */
public class SerializeOperator extends UnaryOperator {

    private final BGP bgp;
    private final String language;

    public SerializeOperator(String operatorName, String fragment, BGP bgp, String language) {
        super(operatorName, fragment);
        this.bgp = bgp;
        this.language = language;
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

        Collection<SolutionMapping> solMappings = m.getSolutionMappings(this.fragment);
        for (SolutionMapping solMapping : solMappings) {
            DatasetGraph graph = this.bgp.apply(solMapping);

            OutputStream outputStream = new ByteArrayOutputStream();
            Lang lang = RDFLanguages.nameToLang(this.language);

            RDFWriter.source(graph)
                    .lang(lang)
                    .output(outputStream);

            // Jena will prepend labels of Blank nodes with a 'B', which is not what we want
            String serialized = outputStream.toString().replaceAll("_:B", "_:");
            SolutionMapping solMapOut = new SolutionMapping(
                    Map.of("?serialized_output", new LiteralNode(serialized)));

            out.addSolutionMap(this.fragment, solMapOut);
        }

        return out;
    }
}

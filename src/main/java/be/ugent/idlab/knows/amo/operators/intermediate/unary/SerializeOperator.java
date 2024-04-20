package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.BGP;
import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.OperatorVisitor;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.Model;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serialize operator will accept a Basic Graph Pattern and replace the variables with the values as provided in the mapping tuple.
 * @param bgp
 */
public class SerializeOperator implements UnaryOperator {

    private final BGP bgp;
    private final String language;

    public SerializeOperator(BGP bgp, String language) {
        this.bgp = bgp;
        this.language = language;
    }

    @Override
    public <T> T accept(OperatorVisitor<T> visitor) {
        return null;
    }

    @Override
    public SolutionMapping apply(SolutionMapping mapping) {
        throw new IllegalStateException("Serialize operator is undefined for SolutionMappings");
    }

    /**
     * Serializes MappingTuple into a graph as described by the BGP, with variables replaced
     * @param m MappingTuple to serialize
     * @param language language to output the serialization in. Supported languages are "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "TURTLE", (and "TTL") and "N3", as supported by Jena's Model::write
     * @return a MappingTuple with the serialization contained in the variable "?serialized_output"
     */
    public MappingTuple apply(MappingTuple m) {
        MappingTuple out = new MappingTuple();

        for (String fragment : m.getFragments()) {
            Collection<SolutionMapping> solMappings = m.getSolutionMappings(fragment);
            for (SolutionMapping solMapping : solMappings) {
                Model model = this.bgp.apply(solMapping);
                OutputStream outputStream = new ByteArrayOutputStream();
                model.write(outputStream, this.language);

                String serialized = outputStream.toString();

                SolutionMapping solMapOut = new SolutionMapping(
                        Map.of("?serialized_output", NodeFactory.createLiteral(serialized)));

                out.addSolutionMap(fragment, solMapOut);
            }
        }

        return out;
    }
}

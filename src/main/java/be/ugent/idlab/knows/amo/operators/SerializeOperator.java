package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.BGP;
import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.Model;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

/**
 * Serialize operator will accept a Basic Graph Pattern and replace the variables with the values as provided in the mapping tuple.
 * @param bgp
 */
public class SerializeOperator {

    private final BGP bgp;

    public SerializeOperator(BGP bgp) {
        this.bgp = bgp;
    }

    /**
     * Serializes MappingTuple into a graph as described by the BGP, with variables replaced
     * @param m MappingTuple to serialize
     * @param language language to output the serialization in. Supported languages are "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "TURTLE", (and "TTL") and "N3", as supported by Jena's Model::write
     * @return a MappingTuple with the serialization contained in the variable "?serialized_output"
     */
    public MappingTuple apply(MappingTuple m, String language) {
        MappingTuple out = new MappingTuple();

        for (String fragment : m.getFragments()) {
            Collection<SolutionMapping> solMappings = m.getSolutionMappings(fragment);
            for (SolutionMapping solMapping : solMappings) {
                Model model = this.bgp.apply(solMapping);
                OutputStream outputStream = new ByteArrayOutputStream();
                model.write(outputStream, language);

                String serialized = outputStream.toString();

                SolutionMapping solMapOut = new SolutionMapping(
                        Map.of("?serialized_output", NodeFactory.createLiteral(serialized)));

                out.addSolutionMap(fragment, solMapOut);
            }
        }

        return out;
    }
}

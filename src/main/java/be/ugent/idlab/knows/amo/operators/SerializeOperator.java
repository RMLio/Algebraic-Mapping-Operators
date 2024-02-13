package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.BGP;
import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import org.apache.jena.rdf.model.Model;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Map;

/**
 * Serialize operator will accept a Basic Graph Pattern and replace the variables with the values as provided in the mapping tuple.
 * @param bgp
 */
public record SerializeOperator(BGP bgp) {

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
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                model.write(outStream, language);

                String outTriples = outStream.toString();

                SolutionMapping solMapOut = new SolutionMapping(
                        Map.of("?serialized_output", outTriples));

                out.addSolutionMap(fragment, solMapOut);
            }
        }

        return out;
    }

    /**
     * Convenience method for serializing into TTL.
     * @see SerializeOperator#apply(MappingTuple, String)
     * @param m mapping tuple to serialize
     * @return
     */
    public MappingTuple apply(MappingTuple m) {
        return apply(m, "TTL");
    }
}

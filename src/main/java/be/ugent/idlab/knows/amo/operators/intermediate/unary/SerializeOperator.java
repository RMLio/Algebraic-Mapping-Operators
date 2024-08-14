package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.BGP;
import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.*;
import org.apache.jena.riot.out.NodeToLabel;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.writer.WriterGraphRIOTBase;
import org.apache.jena.riot.writer.WriterStreamRDFFlat;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphWrapper;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.Symbol;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serial;
import java.util.Collection;
import java.util.Map;

/**
 * Serialize operator will accept a Basic Graph Pattern and replace the variables with the values as provided in the mapping tuple.
 *
 * @param bgp
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
    public SolutionMapping apply(SolutionMapping mapping) {
        throw new IllegalStateException("Serialize operator is undefined for SolutionMappings");
    }

    /**
     * Serializes MappingTuple into a graph as described by the BGP, with variables replaced
     *
     * @param m        MappingTuple to serialize
     * @param language language to output the serialization in. Supported languages are "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "TURTLE", (and "TTL") and "N3", as supported by Jena's Model::write
     * @return a MappingTuple with the serialization contained in the variable "?serialized_output"
     */
    public MappingTuple apply(MappingTuple m) {
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

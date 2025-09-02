package be.ugent.idlab.knows.amo.operators.target.postprocessing;

import org.apache.jena.query.Dataset;
import org.apache.jena.riot.*;

import java.io.StringReader;
import java.io.StringWriter;

/**
 * Utility class to convert serialized RDF from one format to another.
 */
public class RDFFormatter {
    private Dataset dataset = null;
    private final Lang outputLanguage;

    /**
     * Creates a RDFFormatter with a given output language (serialization format).
     * @param outputLanguage    The output language to serialize to.
     */
    public RDFFormatter(Lang outputLanguage) {
        this.outputLanguage = outputLanguage;
    }

    /**
     * Updates the source data and language of this RDFFormatter.
     * @param value An RDF dataset serialized as {@code lang}
     * @param lang  The RDF language (serialization format) of {@code value}
     * @return The RDFFormatter instance with an updated dataset and language.
     */
    public RDFFormatter from(String value, Lang lang) {
        this.dataset = RDFParser.create()
                .source(new StringReader(value))
                .lang(lang)
                .toDataset();
        return this;
    }

    /**
     * Updates the source data of this RDFFormatter and set input language to NQuads.
     * @param value An RDF dataset serialized as {@code lang}
     * @return The RDFFormatter instance with an updated dataset and language set to NQuads.
     */
    public RDFFormatter from(String value) {
        return this.from(value, Lang.NQ);
    }

    /**
     * Serializes the RDF dataset serialized in the given language.
     * @return {@code value} serialized in {@code lang}.
     */
    public String output() {
        if (this.dataset == null) {
            throw new IllegalStateException("Use one of the from* methods to read the dataset first");
        }

        StringWriter writer = new StringWriter();
        RDFDataMgr.write(writer, this.dataset, this.outputLanguage);

        return writer.toString();
    }
}

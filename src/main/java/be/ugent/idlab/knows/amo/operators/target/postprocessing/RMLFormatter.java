package be.ugent.idlab.knows.amo.operators.target.postprocessing;

import org.apache.jena.query.Dataset;
import org.apache.jena.riot.*;

import java.io.StringReader;
import java.io.StringWriter;

/**
 *
 */
public class RMLFormatter {
    private Dataset dataset = null;
    private final Lang outputLanguage;

    public RMLFormatter(Lang outputLanguage) {
        this.outputLanguage = outputLanguage;
    }

    public RMLFormatter from(String value, Lang lang) {
        this.dataset = RDFParser.create()
                .source(new StringReader(value))
                .lang(lang)
                .toDataset();
        return this;
    }

    public RMLFormatter from(String value) {
        return this.from(value, Lang.NQ);
    }

    public String output() {
        if (this.dataset == null) {
            throw new IllegalStateException("Use one of the from* methods to read the dataset first");
        }

        StringWriter writer = new StringWriter();
        RDFDataMgr.write(writer, this.dataset, this.outputLanguage);

        return writer.toString();
    }
}

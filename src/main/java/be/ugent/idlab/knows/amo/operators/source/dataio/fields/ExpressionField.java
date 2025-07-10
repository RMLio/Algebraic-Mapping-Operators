package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;

/**
 * ExpressionField relates to a particular field that holds a value
 */
public class ExpressionField extends Field implements Serializable {

    private final RDFNode constant;

    public ExpressionField(String name, String iterator, RDFNode constant) {
        super(name, iterator);
        this.constant = constant;
    }

    public ExpressionField(String name, RDFNode constant) {
        this(name, null, constant);
    }

    public ExpressionField(String name, String iterator) {
        this(name, iterator, null);
    }

    public RDFNode constant() {
        return constant;
    }

    @Override
    public String toString() {
        return "ExpressionField[name=%s,iterator=%s,constant=%s]".formatted(name, iterator, constant);
    }
}

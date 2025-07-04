package be.ugent.idlab.knows.amo.operators.source.dataio;

import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;

import java.util.Optional;

/**
 * A Field that can be requested from the Source.
 *
 * @param name     namne of the field
 * @param iterator iterator to get to the field by
 * @param constant constant value the field will hold. When this field is different from null
 */
public record Field(String name, String iterator, Optional<RDFNode> constant) {

    /**
     * Constructor that additionally sets the constant field to Empty.
     * @param name name of the field
     * @param iterator iterator to ge tto the field by
     */
    public Field(String name, String iterator) {
        this(name, iterator, Optional.empty());
    }

    /**
     * Constructor where the name of the field is the same as the iterator
     * @param iterator iterator for getting to the value of the field
     */
    public Field(String iterator) {
        this(iterator, iterator, Optional.empty());
    }

    public Field(String name, RDFNode constant) {
        this(name, null, Optional.of(constant));
    }

    /**
     * A shorthand for checking if the value is a constant.
     *
     * When a value is a constant, the iterator field must be ignored.
     * @return true
     */
    public boolean hasConstantValue() {
        return constant.isPresent();
    }

    public RDFNode getConstantValue() {
        if (this.constant.isPresent()) return constant.get();

        throw new IllegalStateException("No constant value present");
    }
}

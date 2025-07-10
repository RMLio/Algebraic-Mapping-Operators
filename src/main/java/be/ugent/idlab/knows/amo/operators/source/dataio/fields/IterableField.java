package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Iterable field does not provide any fields on its own and is simply used to maintain the indexes
 */
public class IterableField extends Field implements Serializable {
    List<Field> subfields = new ArrayList<>();

    public IterableField(String name, String iterator, List<Field> subfields) {
        super(name, iterator);
        this.subfields = new ArrayList<>(subfields);
    }

    public IterableField(String name, String iterator) {
        super(name, iterator);
    }

    public List<Field> getSubfields() {
        return subfields;
    }
}

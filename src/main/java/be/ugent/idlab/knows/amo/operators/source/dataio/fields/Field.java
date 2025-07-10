package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import java.io.Serializable;

public abstract class Field implements Serializable {
    protected final String name;
    protected final String iterator;

    public Field(String name, String iterator) {
        this.name = name;
        this.iterator = iterator;
    }

    public String name() {
        return this.name;
    }

    public String iterator() {
        return this.iterator;
    }

}

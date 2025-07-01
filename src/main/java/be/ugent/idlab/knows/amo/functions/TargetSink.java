package be.ugent.idlab.knows.amo.functions;


import java.io.Serializable;

import org.jspecify.annotations.Nullable;

public interface TargetSink<T> extends Serializable, AutoCloseable {

    /**
     * A function to consume the data provided in the solution mapping into a sink (file, standard output...)
     */
    void sink(@Nullable T data);
}

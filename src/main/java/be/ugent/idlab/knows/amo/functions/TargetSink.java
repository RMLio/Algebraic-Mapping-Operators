package be.ugent.idlab.knows.amo.functions;


import java.io.Serializable;

@FunctionalInterface
public interface TargetSink<T> extends Serializable {

    /**
     * A function to consume the data provided in the solution mapping into a sink (file, standard output...)
     */
    void sink(T data);
}

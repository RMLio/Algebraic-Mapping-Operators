package be.ugent.idlab.knows.amo.functions;


@FunctionalInterface
public interface TargetSink<T> {

    /**
     * A function to consume the data provided in the solution mapping into a sink (file, standard output...)
     */
    void sink(T data);
}

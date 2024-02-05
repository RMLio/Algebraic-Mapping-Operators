package be.ugent.idlab.knows.amo.functions;


import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

@FunctionalInterface
public interface TargetSink {

    /**
     * A function to consume the data provided in the solution mapping into a sink (file, standard output...)
     */
    void sink(SolutionMapping mapping);
}

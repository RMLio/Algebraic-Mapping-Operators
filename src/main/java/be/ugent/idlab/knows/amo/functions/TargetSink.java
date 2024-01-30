package be.ugent.idlab.knows.amo.functions;


import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

@FunctionalInterface
public interface TargetSink {

    /**
     * A function to do something with the data provided in the solution mapping
     */
    void sink(SolutionMapping mapping);
}

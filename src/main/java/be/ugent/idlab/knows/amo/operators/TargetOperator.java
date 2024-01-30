package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.TargetSink;

import java.util.Collection;

public record TargetOperator(String targetFragment, TargetSink sink) {

    public void apply(MappingTuple mappingTuple) {
        Collection<SolutionMapping> solMappings = mappingTuple.getSolutionMappings(targetFragment);
        for (SolutionMapping solMapping : solMappings) {
            sink.sink(solMapping);
        }
    }
}

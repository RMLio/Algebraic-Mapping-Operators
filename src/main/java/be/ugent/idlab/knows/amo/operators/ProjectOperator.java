package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Project operator responsible for restricting the solution mappings.
 */
public class ProjectOperator {

    public SolutionMapping applySolMapping(SolutionMapping mapping, List<String> variables) {
        for (String variable : variables) {
            ((Map<String, Object>) mapping).remove(variable);
        }

        return mapping;
    }

    public Collection<SolutionMapping> applySolMapping(Collection<SolutionMapping> mappings, List<String> variables) {
        for(SolutionMapping m : mappings) {
            applySolMapping(m, variables);
        }

        return mappings;
    }

    public MappingTuple applyMapTuple(MappingTuple tuple, List<String> variables) {
        for(String fragment : tuple.getFragments()) {
            applySolMapping(tuple.getSolutionMapping(fragment), variables);
        }

        return tuple;
    }

    public Collection<MappingTuple> applyMapTuple(Collection<MappingTuple> tuples, List<String> variables) {
        for (MappingTuple t : tuples) {
            applyMapTuple(t, variables);
        }

        return tuples;
    }
}

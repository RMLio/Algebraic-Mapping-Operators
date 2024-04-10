package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.ExtendFunction;
import org.apache.jena.graph.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ExtendOperator will generate new variables (potentially from existing variables) and add these to the SolutionMapping and / or MappingTuple.
 * This is done using the ExtendFunction provided
 *
 */
public class ExtendOperator implements UnaryOperator {

    private final Collection<Pair<String, ExtendFunction>> replacements;

    /**
     * @param variableName name of the variable to replace
     * @param function function to generate the new variable
     */
    public ExtendOperator(Collection<Pair<String, ExtendFunction>> replacements) {
        this.replacements = replacements;
    }

    @Override
    public SolutionMapping applySolMapping(SolutionMapping mapping) {
        SolutionMapping newValues = new SolutionMapping();
        for (Pair<String, ExtendFunction> functionPair : this.replacements) {
            if (!mapping.containsKey(functionPair.first())) {
                newValues.put(functionPair.first(), functionPair.second().apply(mapping));
            }
        }

        return mapping.union(newValues);
    }

    @Override
    public MappingTuple applyMappingTuple(MappingTuple tuple) {
        for (String fragment : tuple.getFragments()) {
            Collection<SolutionMapping> mappings = tuple.getSolutionMappings(fragment);

            List<SolutionMapping> processedMappings = new ArrayList<>();
            for (SolutionMapping mapping : mappings) {
                SolutionMapping processed = applySolMapping(mapping);
                processedMappings.add(processed);
            }

            tuple.setSolutionMaps(fragment, processedMappings);
        }
        return tuple;
    }
}

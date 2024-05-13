package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.functions.ExtendFunction;

import java.io.ObjectInputStream;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ExtendOperator will generate new variables (potentially from existing variables) and add these to the SolutionMapping and / or MappingTuple.
 * This is done using the ExtendFunction provided
 */
public class ExtendOperator implements UnaryOperator {

    private List<Pair<String, ExtendFunction>> replacements;

    public ExtendOperator() {
        this.replacements = new ArrayList<>();
    }


    public ExtendOperator(Collection<Pair<String, ExtendFunction>> replacements) {
        this.replacements = new ArrayList<>();
        this.replacements.addAll(replacements);
    }

    public List<Pair<String, ExtendFunction>> getReplacements() {
        return replacements;
    }

    public void setReplacements(List<Pair<String, ExtendFunction>> replacements) {
        this.replacements = replacements;
    }

    @Serial
    private void readObject(ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
        this.bootstrap();
    }

    private void bootstrap() {

    }

    @Override
    public SolutionMapping apply(SolutionMapping mapping) {
        SolutionMapping newValues = new SolutionMapping();
        for (Pair<String, ExtendFunction> functionPair : this.replacements) {
            if (!mapping.containsKey(functionPair.first())) {
                newValues.put(functionPair.first(), functionPair.second().apply(mapping));
            }
        }

        return mapping.union(newValues);
    }

    @Override
    public MappingTuple apply(MappingTuple tuple) {
        for (String fragment : tuple.getFragments()) {
            List<SolutionMapping> mappings = tuple.getSolutionMappings(fragment).stream().toList();

            List<SolutionMapping> processedMappings = new ArrayList<>();
            for (SolutionMapping m : mappings) {
                SolutionMapping processed = apply(m);
                processedMappings.add(processed);
            }

            tuple.setSolutionMaps(fragment, processedMappings);
        }
        return tuple;
    }
}

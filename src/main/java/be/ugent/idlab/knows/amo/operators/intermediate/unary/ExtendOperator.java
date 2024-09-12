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

import org.jspecify.annotations.Nullable;

/**
 * ExtendOperator will generate new variables (potentially from existing
 * variables) and add these to the SolutionMapping and / or MappingTuple.
 * This is done using the ExtendFunction provided
 */
public class ExtendOperator extends UnaryOperator {

    private List<Pair<String, ExtendFunction>> replacements;

    public ExtendOperator(String operatorName, String fragment) {
        super(operatorName, fragment);
        this.replacements = new ArrayList<>();
    }

    public ExtendOperator(String operatorName, String fragment, Collection<Pair<String, ExtendFunction>> replacements) {
        super(operatorName, fragment);
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
    }

    @Override
    @Nullable
    public SolutionMapping apply(@Nullable SolutionMapping mapping) {
        if (mapping == null){
            return null; 
        }

        SolutionMapping newValues = new SolutionMapping();
        for (Pair<String, ExtendFunction> functionPair : this.replacements) {
            if (!mapping.containsKey(functionPair.first())) {
                newValues.put(functionPair.first(), functionPair.second().applyToNode(mapping));
            }
        }

        return mapping.union(newValues);
    }

    @Override
    public MappingTuple apply(@Nullable MappingTuple tuple) {
        if (tuple == null){
            return null; 
        }

        MappingTuple out = new MappingTuple();

        Collection<SolutionMapping> mappings = tuple.getSolutionMappings(this.fragment);

        List<SolutionMapping> processedMappings = new ArrayList<>();
        for (SolutionMapping m : mappings) {
            SolutionMapping processed = apply(m);
            processedMappings.add(processed);
        }

        out.setSolutionMaps(this.fragment, processedMappings);
        return out;
    }
}

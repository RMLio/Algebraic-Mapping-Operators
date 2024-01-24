package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

import java.util.Set;

public record RenameOperator(Set<Pair> pairs) implements UnaryOperator {

    @Override
    public SolutionMapping applySolMapping(SolutionMapping mapping) {
        SolutionMapping out = new SolutionMapping();
        for (Pair p : this.pairs) {
            for(String key : mapping.keySet()) {
                if (key.equals(p.first())) {
                    out.put(p.second(), mapping.get(key));
                } else {
                    out.put(key, mapping.get(key));
                }
            }
        }

        return out;
    }

    @Override
    public MappingTuple applyMappingTuple(MappingTuple tuple) {
        MappingTuple out = new MappingTuple();
        for (String fragment : tuple.getFragments()) {
            for(SolutionMapping mapping : tuple.getSolutionMappings(fragment)) {
                SolutionMapping newMapping = applySolMapping(mapping);
                out.addSolutionMap(fragment, newMapping);
            }
        }

        return out;
    }
}

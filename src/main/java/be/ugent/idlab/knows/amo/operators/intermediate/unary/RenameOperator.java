package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

import java.util.Collection;

/**
 * RenameOperator will rename the variables.
 *
 * @param pairs a collection of Pairs to rename the variables. The pairs are supplied as instances of Pair(var_to_rename, new_var_name).
 */
public record RenameOperator(Collection<Pair> pairs) implements UnaryOperator {

    @Override
    public SolutionMapping applySolMapping(SolutionMapping mapping) {
        SolutionMapping out = new SolutionMapping();
        for (Pair p : this.pairs) {
            for (String key : mapping.keySet()) {
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
            for (SolutionMapping mapping : tuple.getSolutionMappings(fragment)) {
                SolutionMapping newMapping = applySolMapping(mapping);
                out.addSolutionMap(fragment, newMapping);
            }
        }

        return out;
    }
}

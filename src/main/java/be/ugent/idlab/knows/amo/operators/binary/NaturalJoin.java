package be.ugent.idlab.knows.amo.operators.binary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/* TODO:
    consider rewriting using ThetaJoin
*/
public record NaturalJoin() implements BinaryOperator {
    @Override
    public SolutionMapping applySolMapping(SolutionMapping mapping1, SolutionMapping mapping2) {
        if (mapping1.isCompatibleWith(mapping2)) {
            return mapping1.union(mapping2);
        }

        // TODO: add proper error logging
        System.err.printf("Mappings %s and %s are not compatible, returning empty SolutionMapping!%n", mapping1, mapping2);
        return new SolutionMapping(Map.of()); // if not compatible, return empty union
    }

    @Override
    public MappingTuple applyMapTuple(MappingTuple tuple1, MappingTuple tuple2) {
        MappingTuple out = new MappingTuple();

        Set<String> commonFragments = tuple1.commonFragments(tuple2);
        for (String fragment : commonFragments) {
            Collection<SolutionMapping> sol1 = tuple1.getSolutionMappings(fragment);
            Collection<SolutionMapping> sol2 = tuple2.getSolutionMappings(fragment);

            for (SolutionMapping s1 : sol1) {
                for (SolutionMapping s2 : sol2) {
                    if (s1.isCompatibleWith(s2)) {
                        SolutionMapping merged = s1.union(s2);
                        out.addSolutionMap(fragment, merged);
                    }
                }
            }

        }
        return out;
    }
}

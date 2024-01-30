package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.BGP;
import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;

import java.util.Collection;
import java.util.Map;

public record SerializeOperator(BGP bgp) {

    public MappingTuple apply(MappingTuple m) {
        MappingTuple out = new MappingTuple();

        for (String fragment : m.getFragments()) {
            Collection<SolutionMapping> solMappings = m.getSolutionMappings(fragment);
            for (SolutionMapping solMapping : solMappings) {
                SolutionMapping solMapOut = new SolutionMapping(
                        Map.of("?serialized_output", bgp.apply(solMapping)));

                out.addSolutionMap(fragment, solMapOut);
            }
        }


        return out;
    }
}

package be.ugent.idlab.knows.amo.blocks;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Objects;

/**
 * A relationship between fragments and solution mappings
 */
public class MappingTuple {
    Multimap<String, SolutionMapping> map;

    public MappingTuple() {
        this.map = HashMultimap.create();
    }

    public void addSolutionMap(String fragment, SolutionMapping mapping) {
        this.map.put(fragment, mapping);
    }

    public Collection<SolutionMapping> getSolutionMapping(String fragment) {
        return this.map.get(fragment);
    }

    public Collection<String> getFragments() {
        return this.map.keys();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MappingTuple that = (MappingTuple) o;
        return Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }
}


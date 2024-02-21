package be.ugent.idlab.knows.amo.blocks;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.*;

/**
 * A partial map between fragments and solution mappings.
 */
public class MappingTuple {
    private final Multimap<String, SolutionMapping> map;

    public MappingTuple() {
        this.map = ArrayListMultimap.create();
    }

    // a copy constructor
    public MappingTuple(MappingTuple that) {
        this.map = that.map;
    }

    public Multimap<String, SolutionMapping> getMap() {
        return map;
    }

    /**
     * Add a SolutionMapping to a collection of SolutionMappingss the fragment refers to.
     *
     * @param fragment fragment in question
     * @param mapping  SolutionMapping to add
     */
    public void addSolutionMap(String fragment, SolutionMapping mapping) {
        if (this.map.containsKey(fragment)) {
            Collection<SolutionMapping> present = this.map.get(fragment);
            present.add(mapping);
        } else {
            this.map.put(fragment, mapping);
        }
    }

    /**
     * Set the solution map for a particular fragment, overwriting the previous values
     *
     * @param fragment fragment to overwrite
     * @param mapping  a collection of SolutionMappings the fragment should refer to
     */
    public void setSolutionMaps(String fragment, Collection<SolutionMapping> mapping) {
        this.map.replaceValues(fragment, mapping);
    }

    public void setSolutionMaps(String fragment, SolutionMapping... mapping) {
        this.map.replaceValues(fragment, Arrays.asList(mapping));
    }

    public Collection<SolutionMapping> getSolutionMappings(String fragment) {
        return this.map.get(fragment);
    }

    public Collection<String> getFragments() {
        return this.map.keySet();
    }

    /**
     * Checks compatibility with another Mapping Tuple
     * <p>
     * Two MappingTuples are compatible if, for all common fragments, there is a SolutionMapping that is compatible
     *
     * @param that MappingTuple to compare to
     * @return true if the MappingTuples are compatible, false otherwise
     */
    public boolean isCompatibleWith(MappingTuple that) {
        Set<String> commonFragments = this.map.keySet();
        commonFragments.retainAll(that.getMap().keySet());

        for (String fragment : commonFragments) {
            Collection<SolutionMapping> mappings = this.map.get(fragment);
            Collection<SolutionMapping> thatMappings = that.getSolutionMappings(fragment);
            for (SolutionMapping mapping : mappings) {
                // find at least one mapping that is compatible
                boolean matching = false;
                for (SolutionMapping mapping2 : thatMappings) {
                    // Java's short-circuiting ensures isCompatibleWith won't be called more often than necessary
                    matching = matching || mapping.isCompatibleWith(mapping2);
                }

                // if none found, is not compatible
                if (!matching) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Method to merge another MappingTuple with this MappingTuple.
     */
    public MappingTuple union(MappingTuple that) {
        MappingTuple out = new MappingTuple(this);

        for (String fragment : that.getFragments()) {
            for (SolutionMapping m : that.getSolutionMappings(fragment)) {
                out.addSolutionMap(fragment, m);
            }
        }

        return out;
    }

    /**
     * Find all common fragments with another MappingTuple
     *
     * @param that a MappingTuple
     * @return a set of fragments that are common between the two tuples.
     */
    public Set<String> commonFragments(MappingTuple that) {
        Set<String> fragments = new HashSet<>(this.getFragments());
        fragments.retainAll(that.getFragments());

        return fragments;
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

    private static String foo() {
        return "Bar";
    }
}


package be.ugent.idlab.knows.amo.blocks;

import be.ugent.idlab.knows.amo.util.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

/**
 * A partial map between fragments and solution mappings.
 */
public class MappingTuple implements Serializable {
    private Multimap<String, @Nullable SolutionMapping> map;

    public MappingTuple() {
        this.map = new Multimap<>();
    }

    // a copy constructor
    public MappingTuple(@NonNull MappingTuple that) {
        this.map = that.map;
    }

    public Multimap<String, @Nullable SolutionMapping> getMap() {
        return map;
    }

    public void setMap(Multimap<String, @Nullable SolutionMapping> map) {
        this.map = map;
    }

    public void removeFragment(String fragment) {
        this.map.removeAll(fragment);
    }

    /**
     * Add a SolutionMapping to a collection of SolutionMappings the fragment refers
     * to.
     *
     * @param fragment fragment in question
     * @param mapping  SolutionMapping to add
     */
    public void addSolutionMap(String fragment, @Nullable SolutionMapping mapping) {
        this.map.put(fragment, mapping);
    }

    /**
     * Set the solution map for a particular fragment, overwriting the previous
     * values
     *
     * @param fragment fragment to overwrite
     * @param mapping  a collection of SolutionMappings the fragment should refer to
     */
    public void setSolutionMaps(String fragment, Collection<@Nullable SolutionMapping> mapping) {
        this.map.replaceValues(fragment, mapping);
    }

    public void setSolutionMaps(String fragment, @Nullable SolutionMapping... mapping) {
        this.setSolutionMaps(fragment, Arrays.asList(mapping));
    }

    public Collection<@Nullable SolutionMapping> getSolutionMappings(String fragment) {
        return this.map.get(fragment);
    }

    public Collection<String> getFragments() {
        return this.map.keySet();
    }

    /**
     * Checks compatibility with another Mapping Tuple
     * <p>
     * Two MappingTuples are compatible if, for all common fragments, there is a
     * SolutionMapping that is compatible
     *
     * @param that MappingTuple to compare to
     * @return true if the MappingTuples are compatible, false otherwise
     */
    public boolean isCompatibleWith(@Nullable MappingTuple that) {
        if (that == null) {
            return true;
        }

        Set<String> commonFragments = this.commonFragments(that);

        for (String fragment : commonFragments) {
            Collection<SolutionMapping> mappings = this.map.get(fragment);
            Collection<SolutionMapping> thatMappings = that.getSolutionMappings(fragment);
            for (SolutionMapping mapping : mappings) {
                // find at least one mapping that is compatible
                boolean matching = false;
                for (SolutionMapping mapping2 : thatMappings) {
                    // Java's short-circuiting ensures isCompatibleWith won't be called more often
                    // than necessary
                    matching = matching || mapping.isCompatibleWith(mapping2);
                }

            }
        }

        return true;
    }

    /**
     * Method to merge another MappingTuple with this MappingTuple.
     */
    public MappingTuple union(@Nullable MappingTuple that) {
        if (that == null) {
            return new MappingTuple(this);
        }

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
    public Set<String> commonFragments(@Nullable MappingTuple that) {
        if (that == null) {
            return new HashSet<>();
        }

        Set<String> fragments = new HashSet<>(this.getFragments());
        fragments.retainAll(that.getFragments());

        return fragments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MappingTuple that = (MappingTuple) o;
        for (Map.Entry<String, SolutionMapping> e : this.map.entries()) {
            boolean found = false;
            for (SolutionMapping map : that.map.get(e.getKey())) {
                if (e.getValue().equals(map)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }

        }

        return true;
        // return this.map.asMap().equals(that.map.asMap());
        // return Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this.map.asMap());
    }
}

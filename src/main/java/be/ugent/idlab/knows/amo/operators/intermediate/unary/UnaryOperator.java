package be.ugent.idlab.knows.amo.operators.intermediate.unary;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.operators.OperatorVisitor;
import be.ugent.idlab.knows.amo.operators.intermediate.IntermediateOperator;

import java.util.Collection;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Base interface for all operators that work on individual SolutionMappings and
 * MappingTuples
 */
public abstract class UnaryOperator extends IntermediateOperator {

    /**
     * @param fragment fragment the operator should operate on
     */
    public UnaryOperator(String operatorName, String fragment) {
        super(operatorName, fragment);
    }

    /**
     * Apply the operator on a SolutionMapping
     *
     * @param mapping
     * @return the processed SolutionMapping
     */
    @Nullable
    abstract SolutionMapping apply(@Nullable SolutionMapping mapping);

    /**
     * Apply the operator on the entire collection of SolutionMappings.
     *
     * @param mappings a Collection of SolutionMappings to apply the operator on
     * @return a collection of processed SolutionMappings (internally returned as a
     *         List)
     */
    public Collection<SolutionMapping> applySolMapCollection(Collection<SolutionMapping> mappings) {
        return mappings.stream()
                .map(this::apply)
                .collect(Collectors.toList());
    }

    /**
     * Apply the operator on a MappingTuple.
     *
     * @param tuple tuple to apply the operator on.
     * @return the processed MappingTuple
     */
    @Nullable
    public MappingTuple apply(@Nullable MappingTuple tuple) {
        if (tuple == null) {
            return null;
        }
        MappingTuple out = new MappingTuple();

        for (SolutionMapping map : tuple.getSolutionMappings(this.fragment)) {
            SolutionMapping newMap = this.apply(map);
            out.addSolutionMap(this.fragment, newMap);
        }

        return out;
    }

    /**
     * Apply the operator on the entire collection of MappingTuples.
     *
     * @param tuples a Collection of MappingTuples to apply the operator on.
     * @return a Collection of processed MappingTuples (internally returned as a
     *         List).
     */
    public Collection<MappingTuple> applyMapTupCollection(Collection<MappingTuple> tuples) {
        return tuples.stream().map(this::apply).collect(Collectors.toList());
    }

    @Override
    public <@NonNull T> T accept(@NonNull OperatorVisitor<T> visitor) {
        return visitor.visitUnary(this);
    }
}

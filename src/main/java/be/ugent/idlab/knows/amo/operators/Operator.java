package be.ugent.idlab.knows.amo.operators;

/**
 * Marker interface for all operators
 */
public interface Operator {
    <T> T visit(OperatorVisitor<T> visitor);
}

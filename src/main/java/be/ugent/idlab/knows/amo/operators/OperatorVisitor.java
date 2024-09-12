package be.ugent.idlab.knows.amo.operators;

import org.jspecify.annotations.NullMarked;

import be.ugent.idlab.knows.amo.operators.intermediate.binary.BinaryOperator;
import be.ugent.idlab.knows.amo.operators.intermediate.unary.UnaryOperator;
import be.ugent.idlab.knows.amo.operators.source.SourceOperator;
import be.ugent.idlab.knows.amo.operators.target.TargetOperator;

@NullMarked
public interface OperatorVisitor<T> {

    T visitSource(SourceOperator operator);

    T visitUnary(UnaryOperator operator);

    T visitBinary(BinaryOperator operator);

    T visitTarget(TargetOperator target);
}

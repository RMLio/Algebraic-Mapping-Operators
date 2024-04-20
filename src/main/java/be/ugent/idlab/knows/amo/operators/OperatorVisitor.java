package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.operators.intermediate.unary.ExtendOperator;
import be.ugent.idlab.knows.amo.operators.intermediate.unary.FragmenterOperator;
import be.ugent.idlab.knows.amo.operators.intermediate.unary.ProjectOperator;

public interface OperatorVisitor<T> {

//    T visitExtend(ExtendOperator extendOperator);
//    T visitFragment(FragmenterOperator fragmenterOperator);
    T visitProject(ProjectOperator projectOperator);
}

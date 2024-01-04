package be.ugent.idlab.knows.amo;

import be.ugent.idlab.knows.amo.Operators.Operator;
import org.junit.jupiter.api.Test;


public class ExtendTests extends OperatorTester {

    @Test
    public void Extend_1() {
        RDFValueMap input_smt = new RDFValueMap();
        //smt.add("name",new Resource);
        //smt.add();

        RDFValueMap output_smt = new RDFValueMap();

        Operator op = Operator.getOperator(null);

        testOperator(op, input_smt, output_smt);
    }

}

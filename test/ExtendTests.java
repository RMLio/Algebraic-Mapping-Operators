package test;

import org.junit.jupiter.api.Test;
import src.Config.ConfigComponent;
import src.Operators.Operator;
import src.RDFValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

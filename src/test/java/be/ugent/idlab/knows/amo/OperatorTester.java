package be.ugent.idlab.knows.amo;


import be.ugent.idlab.knows.amo.Config.Config;
import be.ugent.idlab.knows.amo.Operators.Operator;
import be.ugent.idlab.knows.amo.RDFValueMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class OperatorTester {


    public void testOperator(Operator operator, RDFValueMap input, RDFValueMap expectedOutput) {
        RDFValueMap output = operator.process(input);
        assertEquals(output, expectedOutput);


    }

    public void testFiles(String folderPath) throws IOException {

        Path operatorFile = Path.of(folderPath + "/operator.json");
        Path inputFile = Path.of(folderPath + "/input.json");
        Path outputFile = Path.of(folderPath + "/output.json");

        Config operatorConfig = Config.configFromJson(Files.readString(operatorFile));
        String input_tuple = Files.readString(inputFile); // todo parse this into model object.
        String output_tuple = Files.readString(outputFile);

        RDFValueMap input = null;
        RDFValueMap output = null;

        Operator operator = Operator.getOperator(operatorConfig);

        testOperator(operator, input, output);

    }
}

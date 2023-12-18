package test;

import org.eclipse.rdf4j.sparqlbuilder.rdf.Rdf;
import org.junit.jupiter.api.Test;
import src.Config.Component;
import src.Config.ConfigComponent;
import src.Config.ListComponent;
import src.Config.StringComponent;
import src.Operators.Operator;
import src.Operators.ProjectOperator;
import src.RDFValueMap;

import java.util.ArrayList;
import java.util.Map;

public class ProjectTests extends OperatorTester{

    @Test
    public void project_1() {
        RDFValueMap input_smt = new RDFValueMap();
        input_smt.addValue("name", Rdf.literalOf("Ben"));
        input_smt.addValue("sport", Rdf.literalOf("Tennis"));

        RDFValueMap output_smt = new RDFValueMap();
        output_smt.addValue("name", Rdf.literalOf("Ben"));

        ArrayList<Component> components = new ArrayList<>();
        components.add(new StringComponent("name"));

        Operator op = new ProjectOperator(new ConfigComponent(Map.of("projection_attributes", new ListComponent(components))));

        testOperator(op, input_smt, output_smt);
    }

}

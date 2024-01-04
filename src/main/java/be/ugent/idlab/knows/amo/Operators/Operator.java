package be.ugent.idlab.knows.amo.Operators;

import be.ugent.idlab.knows.amo.Config.Component;
import be.ugent.idlab.knows.amo.Config.Config;
import be.ugent.idlab.knows.amo.Config.ConfigComponent;
import be.ugent.idlab.knows.amo.Config.StringComponent;
import be.ugent.idlab.knows.amo.RDFValueMap;

public abstract class Operator {
    public abstract RDFValueMap process(RDFValueMap model); // Edit a model.

    public static Operator getOperator(Config config){

        // get nested config for operator
        Component operator = config.getComponent("operator");


        // formatting is not correct
        if(!(operator instanceof ConfigComponent)){
            throw new Error("Invalid configuration");
        }
        ConfigComponent operatorConfig = (ConfigComponent) operator;

        StringComponent type = (StringComponent) operatorConfig.getComponent("type");
        switch(type.getValue()){
            case "ExtendOp":
                return new ExtendOperator((ConfigComponent) operatorConfig.getComponent("config"));
            case "ProjectOp":
                return new ProjectOperator((ConfigComponent) operatorConfig.getComponent("config"));
        }
        return null;
    }

}

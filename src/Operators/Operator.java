package src.Operators;

import src.Config.Config;
import src.Config.Component;
import src.Config.ConfigComponent;
import src.Config.StringComponent;
import src.RDFValueMap;

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

package be.ugent.idlab.knows.amo.Operators;

import be.ugent.idlab.knows.amo.Config.ConfigComponent;
import be.ugent.idlab.knows.amo.Config.StringComponent;
import be.ugent.idlab.knows.amo.RDFValueMap;

import java.util.ArrayList;
import java.util.List;

public class ExtendOperator extends Operator {

    private interface ExtendFunction {
        void executeFunction(RDFValueMap smt);
    }

    // all possible functions
    private static class Template implements ExtendFunction{
        public Template(ConfigComponent config){


        }
        @Override
        public void executeFunction(RDFValueMap smt) {
            //todo
        }
    }

    private static class Reference implements ExtendFunction{
        private String key;

        public Reference(ConfigComponent config){


        }
        @Override
        public void executeFunction(RDFValueMap smt) {
            //todo
        }
    }

    private static class Constant implements ExtendFunction{
        public Constant(ConfigComponent config){


        }
        @Override
        public void executeFunction(RDFValueMap smt) {
            //todo
        }
    }

    private static class UriEncode implements ExtendFunction{
        public UriEncode(ConfigComponent config){


        }
        @Override
        public void executeFunction(RDFValueMap smt) {
            //todo
        }
    }

    private static class Iri implements ExtendFunction{
        public Iri(ConfigComponent config){


        }
        @Override
        public void executeFunction(RDFValueMap smt) {
            //todo
        }
    }

    private static class Literal implements ExtendFunction{
        public Literal(ConfigComponent config){


        }
        @Override
        public void executeFunction(RDFValueMap smt) {
            //todo
        }
    }

    private List<ExtendFunction> functions = new ArrayList<>();

    // get all extend functions from inside out, so most inside function is executed first.
    private void addFunctions(ConfigComponent config, List<ExtendFunction> functions){
        if(config.hasKey("inner_funtion")){
            // first parse nested functions.
            ConfigComponent nestedConfig = config.tryGetConfig("inner_function");
            addFunctions(nestedConfig, functions);
        }
        StringComponent type = config.tryGetString("type");
        switch (type.getValue().toLowerCase()){
            case "template":
                functions.add(new Template(config));
                break;
            case "reference":
                functions.add(new Reference(config));
                break;
            case "constant":
                functions.add(new Constant(config));
                break;
            case "uriencode":
                functions.add(new UriEncode(config));
                break;
            case "iri":
                functions.add(new Iri(config));
                break;
            case "literal":
                functions.add(new Literal(config));
                break;
        }
    }
    public ExtendOperator(ConfigComponent conf){
        addFunctions(conf, functions);


    }

    private ExtendFunction getFunction(ConfigComponent conf){
        ExtendFunction function = null;
        switch("config[type]"){
            case("UriEncoder"):

                break;

        }

        if(function == null){
            throw new Error("Extend function type not implemented yet");
        }

        return function;
    }

    @Override
    public RDFValueMap process(RDFValueMap object) {

        //copy object
        RDFValueMap result = new RDFValueMap();
        result.copy(object);

        // execute extend functions
        for(ExtendFunction function : functions){
            function.executeFunction(result);
        }

        return result;
    }
}

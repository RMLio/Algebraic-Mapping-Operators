package be.ugent.idlab.knows.amo.Operators;

import be.ugent.idlab.knows.amo.Config.Component;
import be.ugent.idlab.knows.amo.Config.ConfigComponent;
import be.ugent.idlab.knows.amo.Config.ListComponent;
import be.ugent.idlab.knows.amo.Config.StringComponent;
import be.ugent.idlab.knows.amo.RDFValueMap;

import java.util.ArrayList;
import java.util.List;

public class ProjectOperator extends Operator {

    private List<String> keys = new ArrayList<>();

    public ProjectOperator(ConfigComponent config){
        ListComponent list = config.tryGetList("projection_attributes");
        for(Component comp : list.getValue()){
            if(!(comp instanceof StringComponent)){
                throw new Error("config invalid, project attribute is not a string");
            }
            keys.add(((StringComponent) comp).getValue());
        }
    }

    @Override
    public RDFValueMap process(RDFValueMap model) {
        RDFValueMap result = new RDFValueMap();
        for(String s : keys){
            result.addValue(s, model.getValue(s));
        }
        return result;
    }
}

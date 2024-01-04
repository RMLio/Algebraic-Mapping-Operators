package be.ugent.idlab.knows.amo.Config;

import java.util.HashMap;
import java.util.Map;

public class ConfigComponent extends Component {

    private HashMap<String, Component> configValues = new HashMap<>();

    public ConfigComponent(Map<String, Component> config){
        // copy config
        configValues.putAll(config);
    }

    public Component getComponent(String key){
        return configValues.get(key);
    }

    public boolean hasKey(String key){
        return configValues.containsKey(key);
    }

    Component checkForComponent(String key, Class type, String name){
        if(!configValues.containsKey(key)){
            throw new Error("Invalid config, key: " + key + " not found!");
        }
        Component component = getComponent(key);
        if(!type.isInstance(component)){
            throw new Error("Invalid config, key: " + key + " not of type: " + name + " !");
        }
        return component;
    }


    public ConfigComponent tryGetConfig(String key){
        return (ConfigComponent) checkForComponent(key, ConfigComponent.class, "config");
    }

    public StringComponent tryGetString(String key){
        return (StringComponent) checkForComponent(key, StringComponent.class, "string");
    }

    public ListComponent tryGetList(String key){
        return (ListComponent) checkForComponent(key, ListComponent.class, "string");
    }

    public NumeralComponent tryGetNumeral(String key){
        return (NumeralComponent) checkForComponent(key, NumeralComponent.class, "numeral");
    }

    public void setComponent(String key, Component component){
        configValues.put(key, component);
    }
}

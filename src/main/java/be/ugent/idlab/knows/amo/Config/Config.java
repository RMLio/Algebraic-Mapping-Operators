package be.ugent.idlab.knows.amo.Config;

public class Config extends Component {

    private ConfigComponent config;

    public Config(ConfigComponent config){
        this.config = config;
    }

    public static Config configFromJson(String json){
        // todo
        return null;
    }

    public Component getComponent(String key){
        return config.getComponent(key);
    }

}

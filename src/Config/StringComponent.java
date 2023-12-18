package src.Config;

public class StringComponent extends Component {
    private String value;
    public StringComponent(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}

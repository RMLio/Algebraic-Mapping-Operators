package src.Config;

import java.util.List;

public class ListComponent extends Component {
    private List<Component> value;
    public ListComponent(List<Component> value){
        this.value = value;
    }

    public List<Component> getValue(){
        return value;
    }
}

package src.Config;

public class NumeralComponent extends Component {
    private Double value;
    public NumeralComponent(Double value){
        this.value = value;
    }

    public Double getValue(){
        return value;
    }
}

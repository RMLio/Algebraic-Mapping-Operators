package be.ugent.idlab.knows.amo;

import org.eclipse.rdf4j.sparqlbuilder.rdf.RdfValue;

import java.util.HashMap;
import java.util.Map;

public class RDFValueMap {
    protected Map<String, RdfValue> valueMap = new HashMap<>();

    public void copy(RDFValueMap map){
        valueMap.putAll(valueMap);
    }

    public void addValue(String key, RdfValue smt){
        valueMap.put(key, smt);
    }

    public RdfValue getValue(String key){
        return valueMap.get(key);
    }

    // implement equals for testing purposes
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        return valueMap.equals(((RDFValueMap) object).valueMap);
    }

    @Override
    public int hashCode() {
        return valueMap.hashCode();
    }

}

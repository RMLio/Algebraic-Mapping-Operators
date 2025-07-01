package be.ugent.idlab.knows.amo.operators.source.dataio.builders;

import be.ugent.idlab.knows.amo.operators.source.SourceOperator;
import be.ugent.idlab.knows.dataio.access.Access;

/**
 * Builder class for constructing the source operators
 */
public abstract class SourceOperatorBuilder {
    protected String name = "source-operator";
    protected Access access = null;
    protected String fragment = "default";

    public abstract SourceOperator build();

    public static CSVSourceOperatorBuilder CSV() {
        return new CSVSourceOperatorBuilder();
    }

    public static JSONSourceOperatorBuilder JSON() {
        return new JSONSourceOperatorBuilder();
    }

    public static XMLSourceOperatorBuilder XML() {
        return new XMLSourceOperatorBuilder();
    }
}

package be.ugent.idlab.knows.amo.operators.source.dataio.builders;

import be.ugent.idlab.knows.amo.blocks.Pair;
import be.ugent.idlab.knows.amo.blocks.nodes.RDFNode;
import be.ugent.idlab.knows.amo.operators.source.SourceOperator;
import be.ugent.idlab.knows.dataio.access.Access;

import java.util.*;

/**
 * Builder class for constructing the source operators
 */
public abstract class SourceOperatorBuilder {
    protected String name = "source-operator";
    protected Optional<Access> access = Optional.empty();
    protected String fragment = "default";
    protected List<Pair<String, String>> aliases = new ArrayList<>();
    protected Map<String, RDFNode> defaultValues = new HashMap<>();

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

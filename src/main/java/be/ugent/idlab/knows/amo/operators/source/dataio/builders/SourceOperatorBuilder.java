package be.ugent.idlab.knows.amo.operators.source.dataio.builders;

import be.ugent.idlab.knows.amo.operators.source.SourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.fields.Field;
import be.ugent.idlab.knows.dataio.access.Access;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Builder class for constructing the source operators
 */
public abstract class SourceOperatorBuilder {
    protected String name = "source-operator";
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") // access is kept as optional due to some source operators building it on the fly
    protected Optional<Access> access = Optional.empty();
    protected String fragment = "default";
    protected List<Field> fields = new ArrayList<>();

    /**
     * @return the specific builder for a CSVSourceOperatorBuilder
     */
    public static CSVSourceOperatorBuilder CSV() {
        return new CSVSourceOperatorBuilder();
    }

    /**
     * @return the specific builder for a JSONSourceOperator
     */
    public static JSONSourceOperatorBuilder JSON() {
        return new JSONSourceOperatorBuilder();
    }

    /**
     * @return the specific builder for an XMLSourceOperator
     */
    public static XMLSourceOperatorBuilder XML() {
        return new XMLSourceOperatorBuilder();
    }

    public abstract SourceOperator build();

    // a construct to force each builder to implement these methods without losing the specific builder type
    public abstract <T extends SourceOperatorBuilder> T withName(String name);

    public abstract <T extends SourceOperatorBuilder> T withAccess(Access access);

    public abstract <T extends SourceOperatorBuilder> T withFragment(String fragment);

    public abstract <T extends SourceOperatorBuilder> T withField(Field field);

    public abstract <T extends SourceOperatorBuilder> T withFields(Field... fields);
}

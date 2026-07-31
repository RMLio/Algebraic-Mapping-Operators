package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A field keeps its parsers between the objects it reads, which makes it stateful. Reading
 * a series of objects through one field must still produce what a field built per object
 * produces, and a field must survive being serialized, as the operators carrying it are.
 */
public class FieldReuseTest {

    private static final List<String> XML_OBJECTS = List.of(
            "<people><person><name>alice</name></person><person><name>bob</name></person></people>",
            "<people><person><name>carol</name></person></people>",
            "<people><person><name>dave</name></person><person><name>erin</name></person></people>"
    );

    private static final List<String> JSON_OBJECTS = List.of(
            "{\"people\": [{\"name\": \"alice\"}, {\"name\": \"bob\"}]}",
            "{\"people\": [{\"name\": \"carol\"}]}",
            "{\"people\": [{\"name\": \"dave\"}, {\"name\": \"erin\"}]}"
    );

    private static final List<String> CSV_OBJECTS = List.of(
            "name,city\nalice,Ghent\n",
            "name,city\nbob,Brussels\n",
            "name,city\ncarol,Antwerp\n"
    );

    private static Field xmlField() {
        Field name = Field.builder().XML().withName("name").withReference("./person/name").build();
        return new IteratorField("person", List.of(name), ReferenceFormulation.XMLPath, "./people/person");
    }

    private static Field jsonField() {
        Field name = Field.builder().JSON().withName("name").withReference("$.name").build();
        return new IteratorField("person", List.of(name), ReferenceFormulation.JSONPath, "$.people[*]");
    }

    private static Field csvField() {
        return Field.builder().CSV().withName("name").withReference("name").build();
    }

    /**
     * Reads every object through one field, and through a field built per object, and
     * requires the two to agree.
     */
    private void assertReuseMatchesFreshFields(Supplier<Field> factory, List<String> objects) {
        List<List<SolutionMapping>> expected = new ArrayList<>();
        for (String object : objects) {
            expected.add(factory.get().apply(Optional.of(object)));
        }

        Field reused = factory.get();
        List<List<SolutionMapping>> actual = new ArrayList<>();
        for (String object : objects) {
            actual.add(reused.apply(Optional.of(object)));
        }

        assertEquals(expected, actual);
        // guard against the objects being indistinguishable, which would make this vacuous
        assertTrue(expected.stream().distinct().count() > 1, "objects must differ for this to prove anything");
    }

    @SuppressWarnings("unchecked")
    private static <T> T roundTrip(T object) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(object);
        }

        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return (T) in.readObject();
        }
    }

    @Test
    public void xmlFieldReadsEveryObjectTheSame() {
        assertReuseMatchesFreshFields(FieldReuseTest::xmlField, XML_OBJECTS);
    }

    @Test
    public void jsonFieldReadsEveryObjectTheSame() {
        assertReuseMatchesFreshFields(FieldReuseTest::jsonField, JSON_OBJECTS);
    }

    @Test
    public void csvFieldReadsEveryObjectTheSame() {
        assertReuseMatchesFreshFields(FieldReuseTest::csvField, CSV_OBJECTS);
    }

    @Test
    public void aFieldStillReadsAfterBeingSerialized() throws Exception {
        for (Supplier<Field> factory : List.<Supplier<Field>>of(
                FieldReuseTest::xmlField, FieldReuseTest::jsonField, FieldReuseTest::csvField)) {
            List<String> objects = switch (factory.get().getReferenceFormulation()) {
                case XMLPath -> XML_OBJECTS;
                case JSONPath -> JSON_OBJECTS;
                case CSVRows -> CSV_OBJECTS;
            };

            List<SolutionMapping> expected = factory.get().apply(Optional.of(objects.get(0)));

            assertEquals(expected, roundTrip(factory.get()).apply(Optional.of(objects.get(0))));
        }
    }

    @Test
    public void aFieldThatHasReadStillReadsAfterBeingSerialized() throws Exception {
        // the parsers a field holds are not serialized, so one that has already read must
        // build them again on the other side
        Field used = xmlField();
        used.apply(Optional.of(XML_OBJECTS.get(0)));

        Field revived = roundTrip(used);

        assertEquals(xmlField().apply(Optional.of(XML_OBJECTS.get(1))),
                revived.apply(Optional.of(XML_OBJECTS.get(1))));
    }
}

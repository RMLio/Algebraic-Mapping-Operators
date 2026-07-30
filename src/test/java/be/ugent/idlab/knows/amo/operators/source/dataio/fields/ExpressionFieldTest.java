package be.ugent.idlab.knows.amo.operators.source.dataio.fields;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.blocks.nodes.IRINode;
import be.ugent.idlab.knows.amo.blocks.nodes.LiteralNode;
import be.ugent.idlab.knows.amo.functions.ExtendFunction;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the field whose value is produced by a function on the record: a reference, a
 * constant, or any other function, the latter possibly producing several values and hence
 * several records.
 */
public class ExpressionFieldTest {

    /** toUpperCase(name) */
    private static final ExtendFunction TO_UPPER_CASE = solutionMapping -> {
        if (solutionMapping == null || solutionMapping.get("name") == null) {
            return null;
        }
        return solutionMapping.get("name").getValue().toString().toUpperCase();
    };

    /** split(hobbies, ';'), a function producing a value per element. */
    private static final ExtendFunction SPLIT_HOBBIES = new ExtendFunction() {
        @Override
        public String apply(SolutionMapping solutionMapping) {
            List<String> values = this.applyMulti(solutionMapping);
            return values.isEmpty() ? null : values.get(0);
        }

        @Override
        public List<String> applyMulti(SolutionMapping solutionMapping) {
            if (solutionMapping == null || solutionMapping.get("hobbies") == null) {
                return List.of();
            }
            return Arrays.asList(solutionMapping.get("hobbies").getValue().toString().split(";"));
        }
    };

    @Test
    public void appliesFunctionToColumnAndBindsResultToFieldName() {
        Field field = Field.builder()
                .withName("Name")
                .CSV()
                .withExpression(TO_UPPER_CASE)
                .build();

        List<SolutionMapping> result = field.apply(Optional.of("name\nmatthieu\n"));

        assertEquals(1, result.size());
        assertEquals("MATTHIEU", result.get(0).get("Name").getValue().toString());
        // the raw column is not leaked as a variable, only the computed field
        assertNull(result.get(0).get("name"));
    }

    @Test
    public void plainReferenceFieldIsUnaffected() {
        Field field = Field.builder()
                .withName("Name")
                .CSV()
                .withReference("name")
                .build();

        List<SolutionMapping> result = field.apply(Optional.of("name\nmatthieu\n"));

        assertEquals(1, result.size());
        assertEquals("matthieu", result.get(0).get("Name").getValue().toString());
    }

    @Test
    public void referenceAndConstantBuildAnExpressionField() {
        Field reference = Field.builder().withName("Name").CSV().withReference("name").build();
        Field constant = Field.builder().withName("Type").CSV().withConstant(new LiteralNode("person")).build();

        assertInstanceOf(ExpressionField.class, reference);
        assertInstanceOf(ExpressionField.class, constant);
        assertEquals(Optional.of("name"), ((ExpressionField) reference).getReference());
        // a constant reads nothing from the record, so it is not a reference
        assertTrue(((ExpressionField) constant).getReference().isEmpty());
    }

    @Test
    public void functionProducingSeveralValuesProducesARecordPerValue() {
        Field field = Field.builder()
                .withName("Hobby")
                .CSV()
                .withExpression(SPLIT_HOBBIES)
                .build();

        List<SolutionMapping> result = field.apply(Optional.of("name,hobbies\nmatthieu,chess;running;cooking\n"));

        assertEquals(3, result.size());
        assertEquals("chess", result.get(0).get("Hobby").getValue().toString());
        assertEquals("running", result.get(1).get("Hobby").getValue().toString());
        assertEquals("cooking", result.get(2).get("Hobby").getValue().toString());
        // each value is numbered, the way an iterator field numbers its records
        for (int i = 0; i < result.size(); i++) {
            assertEquals(String.valueOf(i), result.get(i).get("Hobby.#").getValue());
        }
    }

    @Test
    public void appliesFunctionToJSONRecord() {
        Field field = Field.builder()
                .withName("Name")
                .JSON()
                .withExpression(TO_UPPER_CASE)
                .build();

        List<SolutionMapping> result = field.apply(Optional.of("{\"name\": \"matthieu\", \"age\": 30}"));

        assertEquals(1, result.size());
        assertEquals("MATTHIEU", result.get(0).get("Name").getValue().toString());
    }

    @Test
    public void appliesFunctionToXMLRecord() {
        // an XML record's variables are paths from the document node, which is what the
        // XML source operator makes a field's references relative to
        Field field = new ExpressionField("Name", List.of(), ReferenceFormulation.XMLPath, TO_UPPER_CASE, "./person");

        List<SolutionMapping> result = field.apply(Optional.of("<person><name>matthieu</name></person>"));

        assertEquals(1, result.size());
        assertEquals("MATTHIEU", result.get(0).get("Name").getValue().toString());
    }

    @Test
    public void xmlSubfieldsReadTheMatchedElement() {
        // a subfield's path starts at the matched element, the way the XML source operator
        // makes a field's references relative to the record's root element
        Field name = Field.builder().XML().withName("name").withReference("./person/name").build();
        Field person = Field.builder().XML().withName("person").withReference("./people/person")
                .withSubfields(name).build();

        List<SolutionMapping> result = person.apply(
                Optional.of("<people><person><name>matthieu</name></person></people>"));

        assertEquals(1, result.size());
        // an XPath applied to the element's text would fail; the subfield gets the element
        assertEquals("matthieu", result.get(0).get("person.name").getValue().toString());
        assertEquals("matthieu", result.get(0).get("person").getValue().toString());
    }

    @Test
    public void xmlFieldWithoutSubfieldsStillBindsTheElementText() {
        Field person = Field.builder().XML().withName("person").withReference("./people/person").build();

        List<SolutionMapping> result = person.apply(
                Optional.of("<people><person><name>matthieu</name><city>Ghent</city></person></people>"));

        assertEquals(1, result.size());
        assertEquals("matthieuGhent", result.get(0).get("person").getValue().toString());
    }

    @Test
    public void constantKeepsTheTermItWasDeclaredAs() {
        Field field = Field.builder()
                .withName("Type")
                .CSV()
                .withConstant(new IRINode("http://example.com/Person"))
                .build();

        List<SolutionMapping> result = field.apply(Optional.of("name\nmatthieu\n"));

        assertEquals(1, result.size());
        assertInstanceOf(IRINode.class, result.get(0).get("Type"));
        assertEquals("http://example.com/Person", result.get(0).get("Type").getValue().toString());
    }

    @Test
    public void fieldMatchingNothingStillBindsItsVariable() {
        Field field = Field.builder()
                .withName("Missing")
                .CSV()
                .withReference("absent")
                .build();

        List<SolutionMapping> result = field.apply(Optional.of("name\nmatthieu\n"));

        assertEquals(1, result.size());
        assertTrue(result.get(0).get("Missing").isNull());
    }
}

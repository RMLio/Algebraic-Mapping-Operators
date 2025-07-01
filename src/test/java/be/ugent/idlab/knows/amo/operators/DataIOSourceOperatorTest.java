package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.operators.source.dataio.CSVSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.JSONSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.XMLSourceOperator;
import be.ugent.idlab.knows.amo.operators.source.dataio.builders.SourceOperatorBuilder;
import be.ugent.idlab.knows.amo.utilities.BlocksIO;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class DataIOSourceOperatorTest {

    @Test
    public void JSONTestSingleField() {
        Access access = new LocalFileAccess("operators/source/input.json", "src/test/resources", "json");
        JSONSourceOperator operator = (JSONSourceOperator) SourceOperatorBuilder.JSON()
                .withAccess(access)
                .withRootVariables(List.of("name"))
                .withRootIterator("$.peoples[*]")
                .withSubIterators(List.of("$.pet.type", "$.pet.name"))
                .build();

        MappingTuple actual = operator.consumeSource();

        MappingTuple expected = BlocksIO.readMappingTuple("operators/source/output.json");

        assertEquals(expected, actual);
    }

    @Test
    public void JSONTestMultipleField() {
        Access access = new LocalFileAccess("operators/source/input.json", "src/test/resources", "json");
        JSONSourceOperator operator = (JSONSourceOperator) SourceOperatorBuilder.JSON()
                .withAccess(access)
                .withRootVariable("name")
                .withRootIterator("$.peoples[*]")
                .withSubIterators(List.of("$.pet.type", "$.pet.name"))
                .build();

        MappingTuple actual = operator.consumeSource();

        MappingTuple expected = BlocksIO.readMappingTuple("operators/source/output.json");

        assertEquals(expected, actual);
    }

    @Test
    public void JSONListInField() {
        Access access = new LocalFileAccess("operators/source/list.json", "src/test/resources", "json");
        JSONSourceOperator operator = (JSONSourceOperator) SourceOperatorBuilder.JSON()
                .withAccess(access)
                .withRootVariables(List.of("ID", "Sport"))
                .withRootIterator("$.students[*]")
                .build();

        MappingTuple actual = operator.consumeSource();

        System.out.println(actual);

    }

    @Test
    public void CSVTest() {
        Access access = new LocalFileAccess("operators/source/input.csv", "src/test/resources", "csv");
        CSVSourceOperator operator = (CSVSourceOperator) SourceOperatorBuilder.CSV()
                .withAccess(access)
                .build();

        MappingTuple actual = operator.consumeSource();
        MappingTuple expected = BlocksIO.readMappingTuple("operators/source/output.json");

        assertEquals(expected, actual);
    }

    @Test
    public void XMLTest() {
        Access access = new LocalFileAccess("operators/source/input.xml", "src/test/resources", "xml");
        XMLSourceOperator operator = (XMLSourceOperator) SourceOperatorBuilder.XML()
                .withAccess(access)
                .withRootVariable("name")
                .withRootIterator("/people/person")
                .withSubIterators(List.of("pet/type", "pet/name"))
                .build();

        MappingTuple actual = operator.consumeSource();
        MappingTuple expected = BlocksIO.readMappingTuple("operators/source/output_xml.json");

        assertEquals(expected, actual);
    }

    @Test
    public void JSONIteratingProductTest() throws Exception {
        Access access = new LocalFileAccess("operators/source/books.json", "src/test/resources", "json");
        JSONSourceOperator operator = (JSONSourceOperator) SourceOperatorBuilder.JSON()
                .withAccess(access)
                .withRootVariables(List.of("authors[*]", "publishers[*]", "title"))
                .withRootIterator("$.books[*]")
                .build();

        operator.init();
        MappingTuple result = operator.consumeSource();

        MappingTuple expected = BlocksIO.readMappingTuple("operators/source/output_iterationtest.json");
        assertEquals(expected, result);
    }

    @Test
    public void aliasCSV() throws SQLException, IOException, ParserConfigurationException, TransformerException {
        Access access = new LocalFileAccess("operators/source/input.csv", "src/test/resources", "csv");
        CSVSourceOperator operator = (CSVSourceOperator) SourceOperatorBuilder.CSV()
                .withAccess(access)
                .withAlias("name", "newName")
                .build();
        operator.init();

        MappingTuple result = operator.consumeSource();
        assertFalse(result.getSolutionMappings("default").isEmpty());
        assertTrue(result.getSolutionMappings("default").stream().findFirst().get().containsKey("newName"));
        assertEquals("John Doe", result.getSolutionMappings("default").stream().findFirst().get().get("newName").getValue());
    }

    @Test
    public void aliasJSON() throws Exception {
        Access access = new LocalFileAccess("operators/source/books.json", "src/test/resources", "json");
        JSONSourceOperator operator = (JSONSourceOperator) SourceOperatorBuilder.JSON()
                .withAccess(access)
                .withRootVariables(List.of("authors[*]", "publishers[*]", "title"))
                .withRootIterator("$.books[*]")
                .withAlias("title", "newTitle")
                .build();

        operator.init();

        MappingTuple result = operator.consumeSource();
        assertFalse(result.getSolutionMappings("default").isEmpty());
        assertTrue(result.getSolutionMappings("default").stream().findFirst().get().containsKey("newTitle"));
        assertEquals("The big rabbit's trip", result.getSolutionMappings("default").stream().findFirst().get().get("newTitle").getValue());
    }

    @Test
    public void aliasXML() {
        Access access = new LocalFileAccess("operators/source/input.xml", "src/test/resources", "xml");
        XMLSourceOperator operator = (XMLSourceOperator) SourceOperatorBuilder.XML().withAccess(access)
                .withRootVariable("name")
                .withRootIterator("/people/person")
                .withRootVariables(List.of("pet/type", "pet/name"))
                .withAlias("pet/name", "newName")
                .build();

        operator.init();
        MappingTuple result = operator.consumeSource();
        assertFalse(result.getSolutionMappings("default").isEmpty());
        assertTrue(result.getSolutionMappings("default").stream().findFirst().get().containsKey("newName"));
        assertEquals("Bax", result.getSolutionMappings("default").stream().findFirst().get().get("newName").getValue());
    }
}

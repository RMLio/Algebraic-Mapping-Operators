package be.ugent.idlab.knows.amo.operators;

import be.ugent.idlab.knows.amo.blocks.MappingTuple;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataIOSourceOperatorTest {

    @Test
    public void simpleTest() {
        Access access = new LocalFileAccess("input.json", "src/test/resources", "json");
        try (JSONSourceIterator iterator = new JSONSourceIterator(access, "$")) {
            DataIOSourceOperator operator = new DataIOSourceOperator(iterator, List.of("Name", "Sport"));
            MappingTuple mappingTuple = operator.getMappingTuples().stream().findFirst().get();

            Collection<String> fragments = mappingTuple.getFragments();
            assertEquals(1, fragments.size());
            String defaultFragment = fragments.stream().toList().get(0);
            assertEquals("f_default", defaultFragment);

            SolutionMapping solMapping = mappingTuple.getSolutionMappings("f_default").stream().findFirst().get();
            assertEquals("Bert", solMapping.get("Name"));
            assertEquals("Badminton", solMapping.get("Sport"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

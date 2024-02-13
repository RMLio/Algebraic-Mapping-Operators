package be.ugent.idlab.knows.amo.performance.bgp;

import be.ugent.idlab.knows.amo.blocks.BGP;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.performance.bgp.classes.BGPStringReplacement;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BGPReplaceTest {
    @Test
    public void timingTestLargeBGP() {
        String subjectPattern = "?john_iri <http://example.com/name> \"John Doe\" .";
        String subjectSolution = "<http://example.com/John> <http://example.com/name> \"John Doe\" .";

        String objectPattern = "<http://example.com/Susan> <http://example.com/name> ?susan_name .";
        String objectSolution = "<http://example.com/Susan> <http://example.com/name> \"Susan\" .";

        String subjectObjectPattern = "?jimmy_iri <http://example.com/name> ?jimmy_name .";
        String subjectObjectSolution = "<http://example.com/Jimmy> <http://example.com/name> \"Jimmy\" .";

        SolutionMapping solMapping = new SolutionMapping(Map.of(
                "?john_iri", "<http://example.com/John>",
                "?susan_name", "\"Susan\"",
                "?jimmy_iri", "<http://example.com/Jimmy>",
                "?jimmy_name", "\"Jimmy\""
        ));

        StringBuilder patternBuilder = new StringBuilder();
        StringBuilder solutionBuilder = new StringBuilder();

        // generate a large pattern
        int limit = 100;
        Random random = new Random(5); // seeded to always get the same pattern and make results comparable
        for (int i = 0; i < limit; i++) {
            // randomly choose between ?a and ?b, place it in the array
            float nextRandom = random.nextFloat();
            if (nextRandom < (1.0 / 3.0)) {
                patternBuilder.append(subjectPattern);
                solutionBuilder.append(subjectSolution);
            } else if (nextRandom < (2.0 / 3.0)) {
                patternBuilder.append(objectPattern);
                solutionBuilder.append(objectSolution);
            } else {
                patternBuilder.append(subjectObjectPattern);
                solutionBuilder.append(subjectObjectSolution);
            }
        }

        String pattern = patternBuilder.toString();
        String expected = solutionBuilder.toString();

        BGPStringReplacement bgpString = new BGPStringReplacement(pattern, solMapping.keySet());

        long before, after;
        before = System.nanoTime();
        String out = bgpString.apply(solMapping);
        after = System.nanoTime();

        assertEquals(expected, out);
        System.out.printf("Time with RegEx: %d ns\n", after - before);

        before = System.nanoTime();
        out = bgpString.applyWithIndex(solMapping);
        after = System.nanoTime();

        assertEquals(expected, out);
        System.out.printf("Time with index: %d ns\n", after - before);

        before = System.nanoTime();
        out = bgpString.applyWithStringUtilsEach(solMapping);
        after = System.nanoTime();

        assertEquals(expected, out);
        System.out.printf("Time with StringUtils::replaceEach : %d ns\n", after - before);

        before = System.nanoTime();
        out = bgpString.applyWithStringUtilsReplace(solMapping);
        after = System.nanoTime();
        assertEquals(expected, out);
        System.out.printf("Time with StringUtils::replace : %d ns\n", after - before);

        before = System.nanoTime();
        BGP bgpJena = new BGP(pattern);
        Model model = bgpJena.apply(solMapping);
        after = System.nanoTime();
        System.out.printf("Time with Model: %d ns\n", after - before);

        System.out.println(model);

    }
}

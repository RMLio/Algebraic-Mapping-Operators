package be.ugent.idlab.knows.amo.performance.bgp;

import be.ugent.idlab.knows.amo.blocks.BGP;
import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.performance.bgp.classes.BGPStringReplacement;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BGPReplaceTest {
    @Test
    public void timingTestLargeBGP() {
        // exactly the same test as above, but only BGP::apply is timed
        String subjectPattern = "?john_iri <http://example.com/name_%f> \"John Doe\" .";
        String subjectSolution = "<http://example.com/John> <http://example.com/name_%f> \"John Doe\" .";

        String objectPattern = "<http://example.com/Susan_%f> <http://example.com/name> ?susan_name .";
        String objectSolution = "<http://example.com/Susan_%f> <http://example.com/name> \"Susan\" .";

        String subjectObjectPattern = "?jimmy_iri <http://example.com/name_%f> ?jimmy_name .";
        String subjectObjectSolution = "<http://example.com/Jimmy> <http://example.com/name_%f> \"Jimmy\" .";

        String predicatePattern = "<http://example.com/Susan_%f> ?name_pred \"Susan\" .";
        String predicateSolution = "<http://example.com/Susan_%f> <http://example.com/name> \"Susan\" .";


        SolutionMapping solMapping = new SolutionMapping(Map.of(
                "?john_iri", "<http://example.com/John>",
                "?susan_name", "\"Susan\"",
                "?jimmy_iri", "<http://example.com/Jimmy>",
                "?jimmy_name", "\"Jimmy\"",
                "?name_pred", "<http://example.com/name>"
        ));

        SolutionMapping solMappingNiceURIs = new SolutionMapping(Map.of(
                "?john_iri", "http://example.com/John",
                "?susan_name", "\"Susan\"",
                "?jimmy_iri", "http://example.com/Jimmy",
                "?jimmy_name", "\"Jimmy\"",
                "?name_pred", "http://example.com/name"
        ));

        StringBuilder patternBuilder = new StringBuilder();
        StringBuilder solutionBuilder = new StringBuilder();

        // generate a large pattern
        int limit = 100000;
        Random random = new Random(5); // seeded to always get the same pattern and make results comparable
        for (int i = 0; i < limit; i++) {
            // randomly choose between ?a and ?b, place it in the array
            float nextRandom = random.nextFloat();
            if (nextRandom < (1.0 / 4.0)) {
                patternBuilder.append(String.format(subjectPattern, nextRandom));
                solutionBuilder.append(String.format(subjectSolution, nextRandom));
            } else if (nextRandom < (2.0 / 4.0)) {
                patternBuilder.append(String.format(objectPattern, nextRandom));
                solutionBuilder.append(String.format(objectSolution, nextRandom));
            } else if (nextRandom < (3.0 / 4.0)) {
                patternBuilder.append(String.format(predicatePattern, nextRandom));
                solutionBuilder.append(String.format(predicateSolution, nextRandom));
            } else {
                patternBuilder.append(String.format(subjectObjectPattern, nextRandom));
                solutionBuilder.append(String.format(subjectObjectSolution, nextRandom));
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

        BGP bgpJena = new BGP(pattern);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // time measurement includes consumption of the model into a string
        before = System.nanoTime();
        Model model = bgpJena.apply(solMappingNiceURIs);
        model.write(outputStream, "TTL");
        after = System.nanoTime();
        System.out.printf("Time with Model: %d ns\n", after - before);
//        System.out.println(outputStream);
    }

    @Test
    public void timingTestVarReplacementJena() {
        // exactly the same test as above, but only BGP::apply is timed
        String subjectPattern = "?john_iri <http://example.com/name_%f> \"John Doe\" .";
        String subjectSolution = "<http://example.com/John> <http://example.com/name_%f> \"John Doe\" .";

        String objectPattern = "<http://example.com/Susan_%f> <http://example.com/name> ?susan_name .";
        String objectSolution = "<http://example.com/Susan_%f> <http://example.com/name> \"Susan\" .";

        String subjectObjectPattern = "?jimmy_iri <http://example.com/name_%f> ?jimmy_name .";
        String subjectObjectSolution = "<http://example.com/Jimmy> <http://example.com/name_%f> \"Jimmy\" .";

        String predicatePattern = "<http://example.com/Susan_%f> ?name_pred \"Susan\" .";
        String predicateSolution = "<http://example.com/Susan_%f> <http://example.com/name> \"Susan\" .";


        SolutionMapping solMapping = new SolutionMapping(Map.of(
                "?john_iri", "<http://example.com/John>",
                "?susan_name", "\"Susan\"",
                "?jimmy_iri", "<http://example.com/Jimmy>",
                "?jimmy_name", "\"Jimmy\"",
                "?name_pred", "<http://example.com/name>"
        ));

        SolutionMapping solMappingNiceURIs = new SolutionMapping(Map.of(
                "?john_iri", "http://example.com/John",
                "?susan_name", "\"Susan\"",
                "?jimmy_iri", "http://example.com/Jimmy",
                "?jimmy_name", "\"Jimmy\"",
                "?name_pred", "http://example.com/name"
        ));

        StringBuilder patternBuilder = new StringBuilder();
        StringBuilder solutionBuilder = new StringBuilder();

        // generate a large pattern
        int limit = 100000;
        Random random = new Random(5); // seeded to always get the same pattern and make results comparable
        for (int i = 0; i < limit; i++) {
            // randomly choose between ?a and ?b, place it in the array
            float nextRandom = random.nextFloat();
            if (nextRandom < (1.0 / 4.0)) {
                patternBuilder.append(String.format(subjectPattern, nextRandom));
                solutionBuilder.append(String.format(subjectSolution, nextRandom));
            } else if (nextRandom < (2.0 / 4.0)) {
                patternBuilder.append(String.format(objectPattern, nextRandom));
                solutionBuilder.append(String.format(objectSolution, nextRandom));
            } else if (nextRandom < (3.0 / 4.0)) {
                patternBuilder.append(String.format(predicatePattern, nextRandom));
                solutionBuilder.append(String.format(predicateSolution, nextRandom));
            } else {
                patternBuilder.append(String.format(subjectObjectPattern, nextRandom));
                solutionBuilder.append(String.format(subjectObjectSolution, nextRandom));
            }
        }

        String pattern = patternBuilder.toString();
        String expected = solutionBuilder.toString();

        BGPStringReplacement bgpString = new BGPStringReplacement(pattern, solMapping.keySet());

        long before, after;
        before = System.currentTimeMillis();
        String out = bgpString.apply(solMapping);
        after = System.currentTimeMillis();

        assertEquals(expected, out);
        System.out.printf("Time with RegEx: %d ms\n", after - before);

        before = System.currentTimeMillis();
        out = bgpString.applyWithIndex(solMapping);
        after = System.currentTimeMillis();

        assertEquals(expected, out);
        System.out.printf("Time with index: %d ms\n", after - before);

        before = System.currentTimeMillis();
        out = bgpString.applyWithStringUtilsEach(solMapping);
        after = System.currentTimeMillis();

        assertEquals(expected, out);
        System.out.printf("Time with StringUtils::replaceEach : %d ms\n", after - before);

        before = System.currentTimeMillis();
        out = bgpString.applyWithStringUtilsReplace(solMapping);
        after = System.currentTimeMillis();
        assertEquals(expected, out);
        System.out.printf("Time with StringUtils::replace : %d ms\n", after - before);

        BGP bgpJena = new BGP(pattern);
        before = System.currentTimeMillis();
        Model model = bgpJena.apply(solMappingNiceURIs);
        after = System.currentTimeMillis();
        System.out.printf("Time with Model: %d ms\n", after - before);
        System.out.printf("Model size: %d\n", model.size());
//        System.out.println(model);
    }
}

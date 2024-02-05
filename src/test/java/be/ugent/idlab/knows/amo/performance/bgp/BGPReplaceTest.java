package be.ugent.idlab.knows.amo.performance.bgp;

import be.ugent.idlab.knows.amo.blocks.SolutionMapping;
import be.ugent.idlab.knows.amo.performance.bgp.classes.BGP;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BGPReplaceTest {

    @Test
    public void timingTestLargeBGP() {
        StringBuilder patternBuilder = new StringBuilder();
        StringBuilder solutionBuilder = new StringBuilder();

        // generate a large pattern
        int limit = 500;
        Random random = new Random(5); // seeded to always get the same pattern and make results comparable
        for (int i = 0; i < limit; i++) {
            // randomly choose between ?a and ?b, place it in the array
            if (random.nextBoolean()) {
                patternBuilder.append("?a ");
                solutionBuilder.append("small ");
            } else {
                patternBuilder.append("?b ");
                solutionBuilder.append("Very Large Replacement String ");
            }
        }

        String pattern = patternBuilder.toString();
        String expected = solutionBuilder.toString();

        SolutionMapping solMapping = new SolutionMapping(Map.of(
                "?a", "small",
                "?b", "Very Large Replacement String"
        ));
        BGP bgp = new BGP(pattern, Set.of("?a", "?b"));

        long before, after;
        before = System.nanoTime();
        String out = bgp.apply(solMapping);
        after = System.nanoTime();

        assertEquals(expected, out);
        System.out.printf("Time with RegEx: %d ns\n", after - before);

        before = System.nanoTime();
        out = bgp.applyWithIndex(solMapping);
        after = System.nanoTime();

        assertEquals(expected, out);
        System.out.printf("Time with index: %d ns\n", after - before);

        before = System.nanoTime();
        out = bgp.applyWithStringUtilsEach(solMapping);
        after = System.nanoTime();

        assertEquals(expected, out);
        System.out.printf("Time with StringUtils::replaceEach : %d ns\n", after - before);

        before = System.nanoTime();
        out = bgp.applyWithStringUtilsReplace(solMapping);
        after = System.nanoTime();
        assertEquals(expected, out);
        System.out.printf("Time with StringUtils::replace : %d ns\n", after - before);
    }
}

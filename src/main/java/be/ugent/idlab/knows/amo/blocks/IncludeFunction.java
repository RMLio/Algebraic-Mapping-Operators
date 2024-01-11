package be.ugent.idlab.knows.amo.blocks;

import java.util.List;

/**
 * Function to describe
 */
public interface IncludeFunction {
    /**
     * Given a Fragment f, decide what variables (if any) should be kept and put in the solution mapping
     * @param f fragment to perform the decision on
     * @return a list of variable names to keep
     */
    List<String> apply(Fragment f);
}

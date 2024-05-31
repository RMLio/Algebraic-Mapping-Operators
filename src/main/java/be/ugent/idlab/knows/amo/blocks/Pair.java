package be.ugent.idlab.knows.amo.blocks;

import java.io.Serializable; /**
 * A record Pair representing a tuple of two strings
 * @param first first string
 * @param second second string
 */
public record Pair<F, S> (F first, S second) implements Serializable  {
}

package be.ugent.idlab.knows.amo.blocks;

import java.io.Serializable; /**
 * A record Pair representing a tuple of two elments
 * @param first first element
 * @param second second element
 */
public record Pair<F, S> (F first, S second) implements Serializable  {
}

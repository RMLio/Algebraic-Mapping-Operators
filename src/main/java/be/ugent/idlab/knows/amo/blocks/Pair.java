package be.ugent.idlab.knows.amo.blocks;

import java.io.Serializable;

/**
 * A record Pair representing a tuple of two elements
 * @param first     The first element
 * @param second    The second element
 * @param <F>       The type of the first element
 * @param <S>       The type of the second element
 */
public record Pair<F, S> (F first, S second) implements Serializable  {
}

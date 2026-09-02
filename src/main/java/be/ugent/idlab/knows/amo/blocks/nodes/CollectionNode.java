package be.ugent.idlab.knows.amo.blocks.nodes;

import org.apache.jena.graph.Node;

import java.util.List;

/**
 * Several RDF terms held as one value.
 * <p>
 * A function may produce more than one value, as splitting a string does. Such a result is
 * one value for as long as it is passed around: a function taking it as an argument sees
 * all of the members, which is what lets one function be applied to the result of another.
 * The members become terms of their own where a term is generated from them, giving a
 * statement per member.
 * <p>
 * A collection is not itself an RDF term and has no Jena node.
 */
public class CollectionNode extends AbstractRDFNode {

    public CollectionNode(List<RDFNode> members) {
        super(List.copyOf(members));
    }

    /**
     * The terms this value stands for, in order.
     */
    @SuppressWarnings("unchecked")
    public List<RDFNode> members() {
        return (List<RDFNode>) this.value;
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public Node getJenaNode() {
        throw new UnsupportedOperationException(
                "A collection of " + members().size() + " terms is not a term itself; "
                        + "generate a term from each of its members instead.");
    }

    @Override
    public String toString() {
        return members().toString();
    }
}

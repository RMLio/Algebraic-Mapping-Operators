package be.ugent.idlab.knows.amo.operators.intermediate.binary;

public sealed interface BinaryType {
    record LeftJoin() implements BinaryType {
    }
    record NaturalJoin() implements BinaryType {
    }
    record CrossJoin() implements BinaryType{
    }
    record ThetaJoin() implements BinaryType {
    }
}



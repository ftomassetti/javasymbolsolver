package com.github.javaparser.symbolsolver.resolution.typeinference;

import java.util.List;

public abstract class Bound {

    class TypeOrWildcard {

    }

    public boolean isSatisfied(InferenceVariable inferenceVariable) {
        throw new UnsupportedOperationException();
    }

    /**
     * Given a bound of the form α = T or T = α, we say T is an instantiation of α.
     */
    public boolean isAnInstantiation() {
        throw new UnsupportedOperationException();
    }

    /**
     * Given a bound of the form α <: T, we say T is a proper upper bound of α.
     */
    public boolean isProperUpperBound() {
        throw new UnsupportedOperationException();
    }

    /**
     * Given a bound of the form T <: α, we say T is a proper lower bound of α.
     */
    public boolean isProperLowerBound() {
        throw new UnsupportedOperationException();
    }

    /**
     * Other bounds relate two inference variables, or an inference variable to a type that contains inference
     * variables. Such bounds, of the form S = T or S <: T, are called dependencies.
     */
    public boolean isADependency() {
        throw new UnsupportedOperationException();
    }

    /**
     * At least one of S or T is an inference variable: S is the same as T
     */
    public class SameAs extends Bound {
        private TIType S;
        private TIType T;
    }

    /**
     * Where at least one of S or T is an inference variable: S is a subtype of T
     */
    public class SubtypeOf extends Bound {
        private TIType S;
        private TIType T;
    }

    /**
     * No valid choice of inference variables exists.
     */
    public class False extends Bound {
        @Override
        public boolean isSatisfied(InferenceVariable inferenceVariable) {
            return false;
        }
    }

    /**
     * Capture(G<A1, ..., An>): The variables α1, ..., αn represent the result of capture conversion (§5.1.10)
     * applied to G<A1, ..., An> (where A1, ..., An may be types or wildcards and may mention inference variables).
     */
    public class Captures extends Bound {
        private List<InferenceVariable> inferenceVariables;
        private List<TypeOrWildcard> typesOrWildcards;
    }

    /**
     * The inference variable α appears in a throws clause.
     */
    public class Throws extends Bound {
        private InferenceVariable inferenceVariable;
    }
}

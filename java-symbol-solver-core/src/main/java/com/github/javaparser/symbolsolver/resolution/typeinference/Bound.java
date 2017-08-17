package com.github.javaparser.symbolsolver.resolution.typeinference;

public abstract class Bound {

    public boolean isSatisfied(InferenceVariable inferenceVariable) {
        throw new UnsupportedOperationException();
    }

    /**
     * At least one of S or T is an inference variable: S is the same as T
     */
    public class SameAs extends Bound {

    }

    /**
     * Where at least one of S or T is an inference variable: S is a subtype of T
     */
    public class SubtypeOf extends Bound {

    }

    /**
     * No valid choice of inference variables exists.
     */
    public class Fail extends Bound {
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

    }

    /**
     * The inference variable α appears in a throws clause.
     */
    public class Throws extends Bound {

    }
}

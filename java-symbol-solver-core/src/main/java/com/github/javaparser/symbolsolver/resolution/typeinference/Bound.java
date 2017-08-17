package com.github.javaparser.symbolsolver.resolution.typeinference;

import com.github.javaparser.symbolsolver.resolution.typeinference.bounds.FalseBound;

public abstract class Bound {

    public static Bound falseBound() {
        return new FalseBound();
    }

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

}

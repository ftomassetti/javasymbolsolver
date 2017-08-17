package com.github.javaparser.symbolsolver.resolution.typeinference;

import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typeinference.bounds.FalseBound;
import com.github.javaparser.utils.Pair;

import java.util.Optional;

public abstract class Bound {

    public static Bound falseBound() {
        return new FalseBound();
    }

    /**
     * A bound is satisfied by an inference variable substitution if, after applying the substitution,
     * the assertion is true.
     */
    public boolean isSatisfied(InferenceVariable inferenceVariable) {
        throw new UnsupportedOperationException();
    }

    /**
     * Given a bound of the form α = T or T = α, we say T is an instantiation of α.
     *
     * Return empty if it is not an instantiation. Otherwise it returns the variable of which this is an
     * instantiation.
     */
    public Optional<Instantiation> isAnInstantiation() {
        return Optional.empty();
    }

    /**
     * Given a bound of the form α <: T, we say T is a proper upper bound of α.
     *
     * Return empty if it is not a proper upper bound. Otherwise it returns the variable of which this is an
     * proper upper bound.
     */
    public Optional<ProperUpperBound> isProperUpperBound() {
        return Optional.empty();
    }

    /**
     * Given a bound of the form T <: α, we say T is a proper lower bound of α.
     *
     * Return empty if it is not a proper lower bound. Otherwise it returns the variable of which this is an
     * proper lower bound.
     */
    public Optional<ProperLowerBound> isProperLowerBound() {
        return Optional.empty();
    }

    /**
     * Other bounds relate two inference variables, or an inference variable to a type that contains inference
     * variables. Such bounds, of the form S = T or S <: T, are called dependencies.
     */
    public boolean isADependency() {
        return false;
    }

}

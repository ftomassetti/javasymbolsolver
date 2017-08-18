package com.github.javaparser.symbolsolver.resolution.typeinference;

import com.github.javaparser.symbolsolver.resolution.typeinference.bounds.FalseBound;

import java.util.Optional;
import java.util.Set;

/**
 * Bound are defined for Inference Variables.
 */
public abstract class Bound {

    public static Bound falseBound() {
        return FalseBound.getInstance();
    }

    /**
     * A bound is satisfied by an inference variable substitution if, after applying the substitution,
     * the assertion is true.
     */
    public abstract boolean isSatisfied(InferenceVariableSubstitution inferenceVariableSubstitution);

    /**
     * Given a bound of the form α = T or T = α, we say T is an instantiation of α.
     *
     * Return empty if it is not an instantiation. Otherwise it returns the variable of which this is an
     * instantiation.
     */
    public Optional<Instantiation> isAnInstantiation() {
        return Optional.empty();
    }

    public boolean isAnInstantiationFor(InferenceVariable v) {
        return isAnInstantiation().isPresent() && isAnInstantiation().get().getInferenceVariable().equals(v);
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

    public Optional<ProperLowerBound> isProperLowerBoundFor(InferenceVariable inferenceVariable) {
        Optional<ProperLowerBound> partial = isProperLowerBound();
        if (partial.isPresent() && partial.get().getInferenceVariable().equals(inferenceVariable)) {
            return partial;
        } else {
            return Optional.empty();
        }
    }

    public Optional<ProperUpperBound> isProperUpperBoundFor(InferenceVariable inferenceVariable) {
        Optional<ProperUpperBound> partial = isProperUpperBound();
        if (partial.isPresent() && partial.get().getInferenceVariable().equals(inferenceVariable)) {
            return partial;
        } else {
            return Optional.empty();
        }
    }

    /**
     * Other bounds relate two inference variables, or an inference variable to a type that contains inference
     * variables. Such bounds, of the form S = T or S <: T, are called dependencies.
     */
    public boolean isADependency() {
        return false;
    }

    public abstract Set<InferenceVariable> usedInferenceVariables();

    public boolean isThrowsBoundOn(InferenceVariable inferenceVariable) {
        return false;
    }
}

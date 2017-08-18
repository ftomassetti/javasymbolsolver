package com.github.javaparser.symbolsolver.resolution.typeinference.bounds;

import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typeinference.Bound;
import com.github.javaparser.symbolsolver.resolution.typeinference.InferenceVariable;
import com.github.javaparser.symbolsolver.resolution.typeinference.ProperLowerBound;
import com.github.javaparser.symbolsolver.resolution.typeinference.ProperUpperBound;
import com.github.javaparser.utils.Pair;

import java.util.Optional;
import java.util.Set;

import static com.github.javaparser.symbolsolver.resolution.typeinference.TypeHelper.isInferenceVariable;
import static com.github.javaparser.symbolsolver.resolution.typeinference.TypeHelper.isProperType;

/**
 * Where at least one of S or T is an inference variable: S is a subtype of T
 */
public class SubtypeOfBound extends Bound {
    private Type s;
    private Type t;

    public Type getS() {
        return s;
    }

    @Override
    public Set<InferenceVariable> usedInferenceVariables() {
        throw new UnsupportedOperationException();
    }

    public Type getT() {
        return t;
    }

    public SubtypeOfBound(Type s, Type t) {
        if (!isInferenceVariable(s) && !isInferenceVariable(t)) {
            throw new IllegalArgumentException("One of S or T should be an inference variable");
        }
        this.s = s;
        this.t = t;
    }

    @Override
    public Optional<ProperUpperBound> isProperUpperBound() {
        if (isInferenceVariable(s) && isProperType(t)) {
            return Optional.of(new ProperUpperBound((InferenceVariable) s, t));
        }
        return Optional.empty();
    }

    @Override
    public Optional<ProperLowerBound> isProperLowerBound() {
        if (isProperType(s) && isInferenceVariable(t)) {
            return Optional.of(new ProperLowerBound((InferenceVariable) t, s));
        }
        return Optional.empty();
    }

    @Override
    public boolean isADependency() {
        return !isProperLowerBound().isPresent() && !isProperUpperBound().isPresent();
    }
}

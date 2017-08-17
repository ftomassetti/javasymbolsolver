package com.github.javaparser.symbolsolver.resolution.typeinference.bounds;

import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typeinference.Bound;

import static com.github.javaparser.symbolsolver.resolution.typeinference.TypeHelper.isInferenceVariable;

/**
 * Where at least one of S or T is an inference variable: S is a subtype of T
 */
public class SubtypeOfBound extends Bound {
    private Type s;
    private Type t;

    public SubtypeOfBound(Type s, Type t) {
        if (!isInferenceVariable(s) && !isInferenceVariable(t)) {
            throw new IllegalArgumentException("One of S or T should be an inference variable");
        }
        this.s = s;
        this.t = t;
    }
}

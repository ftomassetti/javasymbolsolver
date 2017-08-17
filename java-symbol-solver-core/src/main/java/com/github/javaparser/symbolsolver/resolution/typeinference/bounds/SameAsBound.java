package com.github.javaparser.symbolsolver.resolution.typeinference.bounds;

import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typeinference.Bound;
import com.github.javaparser.symbolsolver.resolution.typeinference.TIType;
import com.github.javaparser.symbolsolver.resolution.typeinference.TypeHelper;

import static com.github.javaparser.symbolsolver.resolution.typeinference.TypeHelper.isInferenceVariable;

/**
 * At least one of S or T is an inference variable: S is the same as T
 */
public class SameAsBound extends Bound {
    private Type s;
    private Type t;

    public SameAsBound(Type s, Type t) {
        if (!isInferenceVariable(s) && !isInferenceVariable(t)) {
            throw new IllegalArgumentException("One of S or T should be an inference variable");
        }
        this.s = s;
        this.t = t;
    }
}

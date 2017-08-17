package com.github.javaparser.symbolsolver.resolution.typeinference.bounds;

import com.github.javaparser.symbolsolver.resolution.typeinference.Bound;
import com.github.javaparser.symbolsolver.resolution.typeinference.InferenceVariable;

/**
 * The inference variable α appears in a throws clause.
 */
public class ThrowsBound extends Bound {
    private InferenceVariable inferenceVariable;

    public ThrowsBound(InferenceVariable inferenceVariable) {
        this.inferenceVariable = inferenceVariable;
    }
}

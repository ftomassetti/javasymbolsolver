package com.github.javaparser.symbolsolver.resolution.typeinference.bounds;

import com.github.javaparser.symbolsolver.resolution.typeinference.Bound;
import com.github.javaparser.symbolsolver.resolution.typeinference.InferenceVariable;

import java.util.Set;

/**
 * The inference variable α appears in a throws clause.
 */
public class ThrowsBound extends Bound {
    private InferenceVariable inferenceVariable;

    public ThrowsBound(InferenceVariable inferenceVariable) {
        this.inferenceVariable = inferenceVariable;
    }


    @Override
    public Set<InferenceVariable> usedInferenceVariables() {
        throw new UnsupportedOperationException();
    }
}

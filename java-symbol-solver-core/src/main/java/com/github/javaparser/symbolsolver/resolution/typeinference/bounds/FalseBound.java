package com.github.javaparser.symbolsolver.resolution.typeinference.bounds;

import com.github.javaparser.symbolsolver.resolution.typeinference.Bound;
import com.github.javaparser.symbolsolver.resolution.typeinference.InferenceVariable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * No valid choice of inference variables exists.
 */
public class FalseBound extends Bound {
    @Override
    public boolean isSatisfied(InferenceVariable inferenceVariable) {
        return false;
    }

    @Override
    public Set<InferenceVariable> usedInferenceVariables() {
        return Collections.emptySet();
    }
}

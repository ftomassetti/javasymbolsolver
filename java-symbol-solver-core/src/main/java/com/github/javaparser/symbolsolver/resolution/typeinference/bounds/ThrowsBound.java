package com.github.javaparser.symbolsolver.resolution.typeinference.bounds;

import com.github.javaparser.symbolsolver.resolution.typeinference.Bound;
import com.github.javaparser.symbolsolver.resolution.typeinference.InferenceVariable;

import java.util.Set;

/**
 * The inference variable α appears in a throws clause.
 */
public class ThrowsBound extends Bound {
    private InferenceVariable inferenceVariable;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThrowsBound that = (ThrowsBound) o;

        return inferenceVariable.equals(that.inferenceVariable);
    }

    @Override
    public String toString() {
        return "ThrowsBound{" +
                "inferenceVariable=" + inferenceVariable +
                '}';
    }

    @Override
    public int hashCode() {
        return inferenceVariable.hashCode();
    }

    public ThrowsBound(InferenceVariable inferenceVariable) {
        this.inferenceVariable = inferenceVariable;
    }


    @Override
    public Set<InferenceVariable> usedInferenceVariables() {
        throw new UnsupportedOperationException();
    }
}

package com.github.javaparser.symbolsolver.resolution.typeinference.bounds;

import com.github.javaparser.symbolsolver.resolution.typeinference.Bound;
import com.github.javaparser.symbolsolver.resolution.typeinference.InferenceVariable;
import com.github.javaparser.symbolsolver.resolution.typeinference.InferenceVariableSubstitution;
import com.github.javaparser.symbolsolver.resolution.typeinference.TypeHelper;

import java.util.HashSet;
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

    @Override
    public Set<InferenceVariable> usedInferenceVariables() {
        Set<InferenceVariable> variables = new HashSet<>();
        variables.add(inferenceVariable);
        return variables;
    }

    @Override
    public boolean isSatisfied(InferenceVariableSubstitution inferenceVariableSubstitution) {
        throw new UnsupportedOperationException();
    }
}

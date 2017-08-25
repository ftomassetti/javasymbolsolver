package com.github.javaparser.symbolsolver.resolution.typeinference.bounds;

import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typeinference.Bound;
import com.github.javaparser.symbolsolver.resolution.typeinference.InferenceVariable;
import com.github.javaparser.symbolsolver.resolution.typeinference.InferenceVariableSubstitution;

import java.util.List;
import java.util.Set;

/**
 * Capture(G<A1, ..., An>): The variables α1, ..., αn represent the result of capture conversion (§5.1.10)
 * applied to G<A1, ..., An> (where A1, ..., An may be types or wildcards and may mention inference variables).
 */
public class CapturesBound extends Bound {
    private List<InferenceVariable> inferenceVariables;
    private List<Type> typesOrWildcards;

    public CapturesBound(List<InferenceVariable> inferenceVariables, List<Type> typesOrWildcards) {
        this.inferenceVariables = inferenceVariables;
        this.typesOrWildcards = typesOrWildcards;
    }

    @Override
    public boolean isSatisfied(InferenceVariableSubstitution inferenceVariableSubstitution) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<InferenceVariable> usedInferenceVariables() {
        throw new UnsupportedOperationException();
    }

    public List<InferenceVariable> getInferenceVariables() {
        return inferenceVariables;
    }

    public List<Type> getTypesOrWildcards() {
        return typesOrWildcards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CapturesBound that = (CapturesBound) o;

        if (!inferenceVariables.equals(that.inferenceVariables)) return false;
        return typesOrWildcards.equals(that.typesOrWildcards);
    }

    @Override
    public int hashCode() {
        int result = inferenceVariables.hashCode();
        result = 31 * result + typesOrWildcards.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CapturesBound{" +
                "inferenceVariables=" + inferenceVariables +
                ", typesOrWildcards=" + typesOrWildcards +
                '}';
    }
}

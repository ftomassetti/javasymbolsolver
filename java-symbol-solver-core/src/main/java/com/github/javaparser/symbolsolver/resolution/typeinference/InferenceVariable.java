package com.github.javaparser.symbolsolver.resolution.typeinference;

import com.github.javaparser.symbolsolver.model.typesystem.Type;

import java.util.LinkedList;
import java.util.List;

/**
 * Are meta-variables for types - that is, they are special names that allow abstract reasoning about types.
 * To distinguish them from type variables, inference variables are represented with Greek letters, principally α.
 */
public class InferenceVariable implements Type {

    private String name;
    private static int unnamedInstantiated = 0;

    public InferenceVariable(String name) {
        this.name = name;
    }

    @Override
    public String describe() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InferenceVariable that = (InferenceVariable) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override

    public boolean isAssignableBy(Type other) {
        throw new UnsupportedOperationException();
    }

    public static List<InferenceVariable> instantiate(int size) {
        List<InferenceVariable> inferenceVariables = new LinkedList<>();
        for (int i=0;i<size;i++) {
            inferenceVariables.add(InferenceVariable.unnamed());
        }
        return inferenceVariables;
    }

    private static InferenceVariable unnamed() {
        return new InferenceVariable("__unnamed__" + (unnamedInstantiated++));
    }
}

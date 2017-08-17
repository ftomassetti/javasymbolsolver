package com.github.javaparser.symbolsolver.resolution.typeinference;

import com.github.javaparser.symbolsolver.model.typesystem.Type;

/**
 * Are meta-variables for types - that is, they are special names that allow abstract reasoning about types.
 * To distinguish them from type variables, inference variables are represented with Greek letters, principally α.
 */
public class InferenceVariable implements Type {

    private String name;

    public InferenceVariable(String name) {
        this.name = name;
    }

    @Override
    public String describe() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAssignableBy(Type other) {
        throw new UnsupportedOperationException();
    }
}

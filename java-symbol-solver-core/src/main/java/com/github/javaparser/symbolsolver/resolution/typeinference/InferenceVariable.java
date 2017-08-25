package com.github.javaparser.symbolsolver.resolution.typeinference;

import com.github.javaparser.symbolsolver.model.declarations.TypeParameterDeclaration;
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
    private TypeParameterDeclaration typeParameterDeclaration;

    @Deprecated
    public InferenceVariable(String name) {
        this(name, null);
    }

    public InferenceVariable(String name, TypeParameterDeclaration typeParameterDeclaration) {

        this.name = name;
        this.typeParameterDeclaration = typeParameterDeclaration;
    }

    @Override
    public String describe() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InferenceVariable that = (InferenceVariable) o;

        if (!name.equals(that.name)) return false;
        return typeParameterDeclaration != null ? typeParameterDeclaration.equals(that.typeParameterDeclaration) : that.typeParameterDeclaration == null;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (typeParameterDeclaration != null ? typeParameterDeclaration.hashCode() : 0);
        return result;
    }

    @Override
    public boolean isAssignableBy(Type other) {
        throw new UnsupportedOperationException();
    }

    public static List<InferenceVariable> instantiate(List<TypeParameterDeclaration> typeParameterDeclarations) {
        List<InferenceVariable> inferenceVariables = new LinkedList<>();
        for (TypeParameterDeclaration tp : typeParameterDeclarations) {
            inferenceVariables.add(InferenceVariable.unnamed(tp));
        }
        return inferenceVariables;
    }

    public static InferenceVariable unnamed(TypeParameterDeclaration typeParameterDeclaration) {
        return new InferenceVariable("__unnamed__" + (unnamedInstantiated++), typeParameterDeclaration);
    }

    public TypeParameterDeclaration getTypeParameterDeclaration() {
        if (typeParameterDeclaration == null) {
            throw new UnsupportedOperationException();
        }
        return typeParameterDeclaration;
    }

    @Override
    public String toString() {
        return "InferenceVariable{" +
                "name='" + name + '\'' +
                ", typeParameterDeclaration=" + typeParameterDeclaration +
                '}';
    }

    @Override
    public boolean mention(List<TypeParameterDeclaration> typeParameters) {
        // NOT SURE
        return false;
    }
}

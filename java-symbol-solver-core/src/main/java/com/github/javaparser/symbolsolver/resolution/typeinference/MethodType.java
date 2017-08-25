package com.github.javaparser.symbolsolver.resolution.typeinference;

import com.github.javaparser.symbolsolver.model.declarations.TypeParameterDeclaration;
import com.github.javaparser.symbolsolver.model.methods.MethodUsage;
import com.github.javaparser.symbolsolver.model.typesystem.ReferenceType;
import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.model.typesystem.TypeVariable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * an ordered 4-tuple consisting of:
 – type parameters: the declarations of any type parameters of the method member.
 – argument types: a list of the types of the arguments to the method member.
 – return type: the return type of the method member.
 – throws clause: exception types declared in the throws clause of the method member.

 */
public class MethodType {
    private List<TypeParameterDeclaration> typeParameters;
    private List<Type> formalArgumentTypes;
    private Type returnType;
    private List<ReferenceType> exceptionTypes;

    public MethodType(List<TypeParameterDeclaration> typeParameters, List<Type> formalArgumentTypes, Type returnType, List<ReferenceType> exceptionTypes) {
        this.typeParameters = typeParameters;
        this.formalArgumentTypes = formalArgumentTypes;
        this.returnType = returnType;
        this.exceptionTypes = exceptionTypes;
    }

    public List<TypeParameterDeclaration> getTypeParameters() {
        return typeParameters;
    }

    public List<Type> getFormalArgumentTypes() {
        return formalArgumentTypes;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<ReferenceType> getExceptionTypes() {
        return exceptionTypes;
    }

    public static MethodType fromMethodUsage(MethodUsage methodUsage) {
        return new MethodType(methodUsage.getDeclaration().getTypeParameters(), methodUsage.getParamTypes(), methodUsage.returnType(), methodUsage.exceptionTypes());
    }

    public MethodType replaceTypeVariablesWithInferenceVariables() {
        // Find all type variable
        Map<TypeVariable, InferenceVariable> correspondences = new HashMap<>();
        List<Type> newFormalArgumentTypes = new LinkedList<>();
        for (Type formalArg : formalArgumentTypes) {
            newFormalArgumentTypes.add(replaceTypeVariablesWithInferenceVariables(formalArg, correspondences));
        }
        Type newReturnType = replaceTypeVariablesWithInferenceVariables(returnType, correspondences);
        return new MethodType(typeParameters, newFormalArgumentTypes, newReturnType, exceptionTypes);
    }

    private Type replaceTypeVariablesWithInferenceVariables(Type originalType, Map<TypeVariable, InferenceVariable> correspondences) {
        if (originalType.isTypeVariable()) {
            if (!correspondences.containsKey(originalType.asTypeVariable())) {
                correspondences.put(originalType.asTypeVariable(), InferenceVariable.unnamed(originalType.asTypeVariable().asTypeParameter()));
            }
            return correspondences.get(originalType.asTypeVariable());
        }
        if (originalType.isPrimitive()) {
            return originalType;
        }
        throw new UnsupportedOperationException(originalType.toString());
    }
}

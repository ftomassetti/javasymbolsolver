package com.github.javaparser.symbolsolver.resolution.typeinference;

import com.github.javaparser.symbolsolver.model.declarations.TypeParameterDeclaration;
import com.github.javaparser.symbolsolver.model.methods.MethodUsage;
import com.github.javaparser.symbolsolver.model.typesystem.ReferenceType;
import com.github.javaparser.symbolsolver.model.typesystem.Type;

import java.util.List;

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
}

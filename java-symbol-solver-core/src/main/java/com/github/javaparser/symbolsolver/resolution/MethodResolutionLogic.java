/*
 * Copyright 2016 Federico Tomassetti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.javaparser.symbolsolver.resolution;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.symbolsolver.core.resolution.Context;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserAnonymousClassDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserClassDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserEnumDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserInterfaceDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.github.javaparser.symbolsolver.javassistmodel.JavassistClassDeclaration;
import com.github.javaparser.symbolsolver.javassistmodel.JavassistEnumDeclaration;
import com.github.javaparser.symbolsolver.javassistmodel.JavassistInterfaceDeclaration;
import com.github.javaparser.symbolsolver.model.declarations.*;
import com.github.javaparser.symbolsolver.model.declarations.MethodDeclaration;
import com.github.javaparser.symbolsolver.model.declarations.TypeDeclaration;
import com.github.javaparser.symbolsolver.model.methods.MethodUsage;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.model.typesystem.*;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionClassDeclaration;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionEnumDeclaration;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionInterfaceDeclaration;
import com.github.javaparser.symbolsolver.resolution.typeinference.TypeInference;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Federico Tomassetti
 */
public class MethodResolutionLogic {

    // TODO move to a different class
    public static Type replaceTypeParam(Type type, TypeParameterDeclaration tp, TypeSolver typeSolver) {
        if (type.isTypeVariable()) {
            if (type.describe().equals(tp.getName())) {
                List<TypeParameterDeclaration.Bound> bounds = tp.getBounds(typeSolver);
                if (bounds.size() > 1) {
                    throw new UnsupportedOperationException();
                } else if (bounds.size() == 1) {
                    return bounds.get(0).getType();
                } else {
                    return new ReferenceTypeImpl(typeSolver.solveType(Object.class.getCanonicalName()), typeSolver);
                }
            }
            return type;
        } else if (type.isPrimitive()) {
            return type;
        } else if (type.isArray()) {
            return new ArrayType(replaceTypeParam(type.asArrayType().getComponentType(), tp, typeSolver));
        } else if (type.isReferenceType()) {
            ReferenceType result = type.asReferenceType();
            result = result.transformTypeParameters(typeParam -> replaceTypeParam(typeParam, tp, typeSolver)).asReferenceType();
            return result;
        } else if (type.isWildcard()) {
            if (type.describe().equals(tp.getName())) {
                List<TypeParameterDeclaration.Bound> bounds = tp.getBounds(typeSolver);
                if (bounds.size() > 1) {
                    throw new UnsupportedOperationException();
                } else if (bounds.size() == 1) {
                    return bounds.get(0).getType();
                } else {
                    return new ReferenceTypeImpl(typeSolver.solveType(Object.class.getCanonicalName()), typeSolver);
                }
            }
            return type;
        } else {
            throw new UnsupportedOperationException("Replacing " + type + ", param " + tp + " with " + type.getClass().getCanonicalName());
        }
    }

    // USED ONLY IN DEPRECATED METHODS
    /**
     * @param methods        we expect the methods to be ordered such that inherited methods are later in the list
     * @param name
     * @param argumentsTypes
     * @param typeSolver
     * @return
     */
    @Deprecated
    public static SymbolReference<MethodDeclaration> findMostApplicable(List<MethodDeclaration> methods, String name,
                                                                        List<Type> argumentsTypes,
                                                                        TypeSolver typeSolver) {
        SymbolReference<MethodDeclaration> res = findMostApplicable(methods, name, argumentsTypes, typeSolver, false);
        if (res.isSolved()) {
            return res;
        }
        return findMostApplicable(methods, name, argumentsTypes, typeSolver, true);
    }

    // USED ONLY IN DEPRECATED METHODS
    @Deprecated
    public static SymbolReference<MethodDeclaration> findMostApplicable(List<MethodDeclaration> methods, String name,
                                                                        List<Type> argumentsTypes,
                                                                        TypeSolver typeSolver,
                                                                        boolean wildcardTolerance) {
        List<MethodDeclaration> applicableMethods = getMethodsWithoutDuplicates(methods).stream().filter((m) -> isApplicable(m, name, argumentsTypes, typeSolver, wildcardTolerance)).collect(Collectors.toList());
        if (applicableMethods.isEmpty()) {
            return SymbolReference.unsolved(MethodDeclaration.class);
        }

        if (applicableMethods.size() > 1) {
          List<Integer> nullParamIndexes = new ArrayList<>();
          for (int i = 0; i < argumentsTypes.size(); i++) {
            if (argumentsTypes.get(i).isNull()) {
              nullParamIndexes.add(i);
            }
          }
          if (!nullParamIndexes.isEmpty()) {
            // remove method with array param if a non array exists and arg is null
            Set<MethodDeclaration> removeCandidates = new HashSet<>();
            for (Integer nullParamIndex: nullParamIndexes) {
              for (MethodDeclaration methDecl: applicableMethods) {
                if (methDecl.getParam(nullParamIndex.intValue()).getType().isArray()) {
                  removeCandidates.add(methDecl);
                }
              }
            }
            if (!removeCandidates.isEmpty() && removeCandidates.size() < applicableMethods.size()) {
              applicableMethods.removeAll(removeCandidates);
            }
          }
        }
        if (applicableMethods.size() == 1) {
            return SymbolReference.solved(applicableMethods.get(0));
        } else {
            MethodDeclaration winningCandidate = applicableMethods.get(0);
            MethodDeclaration other = null;
            boolean possibleAmbiguity = false;
            for (int i = 1; i < applicableMethods.size(); i++) {
                other = applicableMethods.get(i);
                if (isMoreSpecific(winningCandidate, other, argumentsTypes, typeSolver)) {
                    possibleAmbiguity = false;
                } else if (isMoreSpecific(other, winningCandidate, argumentsTypes, typeSolver)) {
                    possibleAmbiguity = false;
                    winningCandidate = other;
                } else {
                    if (winningCandidate.declaringType().getQualifiedName().equals(other.declaringType().getQualifiedName())) {
                        possibleAmbiguity = true;
                    } else {
                        // we expect the methods to be ordered such that inherited methods are later in the list
                    }
                }
            }
            if (possibleAmbiguity) {
              // pick the first exact match if it exists
              if (!isExactMatch(winningCandidate, argumentsTypes)) {
                if (isExactMatch(other, argumentsTypes)) {
                  winningCandidate = other;
                } else {
                  throw new MethodAmbiguityException("Ambiguous method call: cannot find a most applicable method: " + winningCandidate + ", " + other);
                }
              }
            }
            return SymbolReference.solved(winningCandidate);
        }
    }

    private static boolean moreSpecific(MethodCallExpr methodCall, MethodDeclaration m1, MethodDeclaration m2,
                                       TypeSolver typeSolver) {
        if (m2.isGeneric()) {
            return new TypeInference(typeSolver).moreSpecificMethodInference(methodCall, m1, m2);
        } else {
            return moreSpecificWithoutGeneric(methodCall, m1, m2, typeSolver);
        }
    }

    private static boolean moreSpecificWithoutGeneric(MethodCallExpr methodCall, MethodDeclaration m1, MethodDeclaration m2,
                                                      TypeSolver typeSolver) {
                List<Type> argumentsTypes = methodCall.getArguments().stream()
                .map(a -> JavaParserFacade.get(typeSolver).getType(a))
                .collect(Collectors.toList());
        List<Integer> nullParamIndexes = new ArrayList<>();
        for (int i = 0; i < methodCall.getArguments().size(); i++) {
            if (argumentsTypes.get(i).isNull()) {
                nullParamIndexes.add(i);
            }
        }
        if (!nullParamIndexes.isEmpty()) {
            // remove method with array param if a non array exists and arg is null
            Set<MethodDeclaration> removeCandidates = new HashSet<>();
            boolean excludeM1 = false;
            boolean excludeM2 = false;
            for (Integer nullParamIndex: nullParamIndexes) {
                excludeM1 = excludeM1 || m1.getParam(nullParamIndex.intValue()).getType().isArray();
                excludeM2 = excludeM2 || m2.getParam(nullParamIndex.intValue()).getType().isArray();
            }
            if (excludeM2 && !excludeM1) {
                return true;
            }
            if (excludeM1 && !excludeM2) {
                return false;
            }
        }

        return isMoreSpecific(m1, m2, argumentsTypes, typeSolver);
    }

    public static SymbolReference<MethodDeclaration> findMostApplicable(List<MethodDeclaration> methods,
                                                                        MethodCallExpr methodCall,
                                                                        TypeSolver typeSolver) {
        System.out.println("DEBUG CALL TO findMostApplicable "+ methods+ " call="+methodCall);
        List<MethodDeclaration> applicableMethods = getMethodsWithoutDuplicates(methods).stream()
                .filter((m) -> isApplicable(m, methodCall, typeSolver)).collect(Collectors.toList());
        if (applicableMethods.isEmpty()) {
            return SymbolReference.unsolved(MethodDeclaration.class);
        }

        if (applicableMethods.isEmpty()) {
            return SymbolReference.solved(applicableMethods.get(0));
        }

        MethodDeclaration most = applicableMethods.get(0);
        for (int i=1;i<applicableMethods.size();i++) {
            if (moreSpecific(methodCall, most, applicableMethods.get(i), typeSolver)) {
                most = applicableMethods.get(i);
            } /*else if (!moreSpecific(methodCall, applicableMethods.get(i), most, typeSolver)) {
                throw new MethodAmbiguityException("Ambiguous method call: cannot find a most applicable method: " + most + ", " + applicableMethods.get(i));
            }*/
        }
        return SymbolReference.solved(most);

//        List<Type> argumentsTypes = methodCall.getArguments().stream()
//                .map(a -> JavaParserFacade.get(typeSolver).getType(a))
//                .collect(Collectors.toList());
//        if (applicableMethods.size() > 1) {
//            List<Integer> nullParamIndexes = new ArrayList<>();
//            for (int i = 0; i < methodCall.getArguments().size(); i++) {
//                if (argumentsTypes.get(i).isNull()) {
//                    nullParamIndexes.add(i);
//                }
//            }
//            if (!nullParamIndexes.isEmpty()) {
//                // remove method with array param if a non array exists and arg is null
//                Set<MethodDeclaration> removeCandidates = new HashSet<>();
//                for (Integer nullParamIndex: nullParamIndexes) {
//                    for (MethodDeclaration methDecl: applicableMethods) {
//                        if (methDecl.getParam(nullParamIndex.intValue()).getType().isArray()) {
//                            removeCandidates.add(methDecl);
//                        }
//                    }
//                }
//                if (!removeCandidates.isEmpty() && removeCandidates.size() < applicableMethods.size()) {
//                    applicableMethods.removeAll(removeCandidates);
//                }
//            }
//        }
//        if (applicableMethods.size() == 1) {
//            return SymbolReference.solved(applicableMethods.get(0));
//        } else {
//            MethodDeclaration winningCandidate = applicableMethods.get(0);
//            MethodDeclaration other = null;
//            boolean possibleAmbiguity = false;
//            for (int i = 1; i < applicableMethods.size(); i++) {
//                other = applicableMethods.get(i);
//                if (isMoreSpecific(winningCandidate, other, argumentsTypes, typeSolver)) {
//                    possibleAmbiguity = false;
//                } else if (isMoreSpecific(other, winningCandidate, argumentsTypes, typeSolver)) {
//                    possibleAmbiguity = false;
//                    winningCandidate = other;
//                } else {
//                    if (winningCandidate.declaringType().getQualifiedName().equals(other.declaringType().getQualifiedName())) {
//                        possibleAmbiguity = true;
//                    } else {
//                        // we expect the methods to be ordered such that inherited methods are later in the list
//                    }
//                }
//            }
//            if (possibleAmbiguity) {
//                // pick the first exact match if it exists
//                if (!isExactMatch(winningCandidate, argumentsTypes)) {
//                    if (isExactMatch(other, argumentsTypes)) {
//                        winningCandidate = other;
//                    } else {
//                        throw new MethodAmbiguityException("Ambiguous method call: cannot find a most applicable method: " + winningCandidate + ", " + other);
//                    }
//                }
//            }
//            return SymbolReference.solved(winningCandidate);
//        }
    }

    public static Optional<MethodUsage> findMostApplicableUsage(List<MethodUsage> methods,
                                                                MethodCallExpr methodCall,
                                                                TypeSolver typeSolver) {
        List<MethodUsage> applicableMethods = methods.stream().filter((m) -> isApplicable(m.getDeclaration(), methodCall, typeSolver)).collect(Collectors.toList());

        if (applicableMethods.isEmpty()) {
            return Optional.empty();
        }
        if (applicableMethods.size() == 1) {
            return Optional.of(applicableMethods.get(0));
        } else {
            MethodUsage winningCandidate = applicableMethods.get(0);
            for (int i = 1; i < applicableMethods.size(); i++) {
                MethodUsage other = applicableMethods.get(i);
                if (isMoreSpecific(winningCandidate, other, typeSolver)) {
                    // nothing to do
                } else if (isMoreSpecific(other, winningCandidate, typeSolver)) {
                    winningCandidate = other;
                } else {
                    if (winningCandidate.declaringType().getQualifiedName().equals(other.declaringType().getQualifiedName())) {
                        if (!areOverride(winningCandidate, other)) {
                            throw new MethodAmbiguityException("Ambiguous method call: cannot find a most applicable method: " + winningCandidate + ", " + other + ". First declared in " + winningCandidate.declaringType().getQualifiedName());
                        }
                    } else {
                        // we expect the methods to be ordered such that inherited methods are later in the list
                        //throw new UnsupportedOperationException();
                    }
                }
            }
            return Optional.of(winningCandidate);
        }
    }


    // USED ONLY IN DEPRECATED METHODS
    @Deprecated
    public static Optional<MethodUsage> findMostApplicableUsage(List<MethodUsage> methods, String name,
                                                                List<Type> argumentsTypes, TypeSolver typeSolver) {
        List<MethodUsage> applicableMethods = methods.stream()
                .filter((m) -> isApplicable(m, name, argumentsTypes, typeSolver)).collect(Collectors.toList());

        if (applicableMethods.isEmpty()) {
            return Optional.empty();
        }
        if (applicableMethods.size() == 1) {
            return Optional.of(applicableMethods.get(0));
        } else {
            MethodUsage winningCandidate = applicableMethods.get(0);
            for (int i = 1; i < applicableMethods.size(); i++) {
                MethodUsage other = applicableMethods.get(i);
                if (isMoreSpecific(winningCandidate, other, typeSolver)) {
                    // nothing to do
                } else if (isMoreSpecific(other, winningCandidate, typeSolver)) {
                    winningCandidate = other;
                } else {
                    if (winningCandidate.declaringType().getQualifiedName()
                            .equals(other.declaringType().getQualifiedName())) {
                        if (!areOverride(winningCandidate, other)) {
                            throw new MethodAmbiguityException(
                                    "Ambiguous method call: cannot find a most applicable method: " + winningCandidate
                                            + ", " + other + ". First declared in "
                                            + winningCandidate.declaringType().getQualifiedName());
                        }
                    } else {
                        // we expect the methods to be ordered such that inherited methods are later in the list
                        //throw new UnsupportedOperationException();
                    }
                }
            }
            return Optional.of(winningCandidate);
        }
    }

    // USED ONLY IN DEPRECATED METHODS
    @Deprecated
    public static SymbolReference<MethodDeclaration> solveMethodInType(TypeDeclaration typeDeclaration, String name,
                                                                       List<Type> argumentsTypes,
                                                                       TypeSolver typeSolver) {
        return solveMethodInType(typeDeclaration, name, argumentsTypes, false, typeSolver);
    }

    public static SymbolReference<MethodDeclaration> solveMethodInType(
            TypeDeclaration typeDeclaration, MethodCallExpr call, TypeSolver typeSolver) {
        return solveMethodInType(typeDeclaration, call, false, typeSolver);
    }

    public static SymbolReference<MethodDeclaration> solveMethodInType(
            TypeDeclaration typeDeclaration, MethodCallExpr call, boolean staticOnly, TypeSolver typeSolver) {
        if (typeDeclaration instanceof JavaParserClassDeclaration) {
            Context ctx = ((JavaParserClassDeclaration) typeDeclaration).getContext();
            return ctx.solveMethod(call, staticOnly, typeSolver);
        }
        if (typeDeclaration instanceof JavaParserInterfaceDeclaration) {
            Context ctx = ((JavaParserInterfaceDeclaration) typeDeclaration).getContext();
            return ctx.solveMethod(call, staticOnly, typeSolver);
        }
        if (typeDeclaration instanceof JavaParserEnumDeclaration) {
            if (call.getName().equals("values") && call.getArguments().isEmpty()) {
                return SymbolReference.solved(new JavaParserEnumDeclaration.ValuesMethod((JavaParserEnumDeclaration) typeDeclaration, typeSolver));
            }
            Context ctx = ((JavaParserEnumDeclaration) typeDeclaration).getContext();
            return ctx.solveMethod(call, staticOnly, typeSolver);
        }
        if (typeDeclaration instanceof JavaParserAnonymousClassDeclaration) {
            Context ctx = ((JavaParserAnonymousClassDeclaration) typeDeclaration).getContext();
            return ctx.solveMethod(call, staticOnly, typeSolver);
        }
        if (typeDeclaration instanceof ReflectionClassDeclaration) {
            return ((ReflectionClassDeclaration) typeDeclaration).solveMethod(call, staticOnly);
        }
        if (typeDeclaration instanceof ReflectionInterfaceDeclaration) {
            return ((ReflectionInterfaceDeclaration) typeDeclaration).solveMethod(call, staticOnly);
        }
        if (typeDeclaration instanceof ReflectionEnumDeclaration) {
            return ((ReflectionEnumDeclaration) typeDeclaration).solveMethod(call, staticOnly);
        }
        if (typeDeclaration instanceof JavassistInterfaceDeclaration) {
            return ((JavassistInterfaceDeclaration) typeDeclaration).solveMethod(call, staticOnly);
        }
        if (typeDeclaration instanceof JavassistClassDeclaration) {
            return ((JavassistClassDeclaration) typeDeclaration).solveMethod(call, staticOnly);
        }
        if (typeDeclaration instanceof JavassistEnumDeclaration) {
            return ((JavassistEnumDeclaration) typeDeclaration).solveMethod(call, staticOnly);
        }
        throw new UnsupportedOperationException(typeDeclaration.getClass().getCanonicalName());
    }

    // USED ONLY IN DEPRECATED METHODS
    /**
     * Replace TypeDeclaration.solveMethod
     *
     * @param typeDeclaration
     * @param name
     * @param argumentsTypes
     * @param staticOnly
     * @return
     */
    @Deprecated
    public static SymbolReference<MethodDeclaration> solveMethodInType(
            TypeDeclaration typeDeclaration, String name, List<Type> argumentsTypes, boolean staticOnly,
            TypeSolver typeSolver) {
        if (typeDeclaration instanceof JavaParserClassDeclaration) {
            Context ctx = ((JavaParserClassDeclaration) typeDeclaration).getContext();
            return ctx.solveMethod(name, argumentsTypes, staticOnly, typeSolver);
        }
        if (typeDeclaration instanceof JavaParserInterfaceDeclaration) {
            Context ctx = ((JavaParserInterfaceDeclaration) typeDeclaration).getContext();
            return ctx.solveMethod(name, argumentsTypes, staticOnly, typeSolver);
        }
        if (typeDeclaration instanceof JavaParserEnumDeclaration) {
            if (name.equals("values") && argumentsTypes.isEmpty()) {
                return SymbolReference.solved(new JavaParserEnumDeclaration.ValuesMethod((JavaParserEnumDeclaration) typeDeclaration, typeSolver));
            }
            Context ctx = ((JavaParserEnumDeclaration) typeDeclaration).getContext();
            return ctx.solveMethod(name, argumentsTypes, staticOnly, typeSolver);
        }
        if (typeDeclaration instanceof JavaParserAnonymousClassDeclaration) {
        	Context ctx = ((JavaParserAnonymousClassDeclaration) typeDeclaration).getContext();
            return ctx.solveMethod(name, argumentsTypes, staticOnly, typeSolver);
        }
        if (typeDeclaration instanceof ReflectionClassDeclaration) {
            return ((ReflectionClassDeclaration) typeDeclaration).solveMethod(name, argumentsTypes, staticOnly);
        }
        if (typeDeclaration instanceof ReflectionInterfaceDeclaration) {
          return ((ReflectionInterfaceDeclaration) typeDeclaration).solveMethod(name, argumentsTypes, staticOnly);
        }
          if (typeDeclaration instanceof ReflectionEnumDeclaration) {
            return ((ReflectionEnumDeclaration) typeDeclaration).solveMethod(name, argumentsTypes, staticOnly);
        }
        if (typeDeclaration instanceof JavassistInterfaceDeclaration) {
            return ((JavassistInterfaceDeclaration) typeDeclaration).solveMethod(name, argumentsTypes, staticOnly);
        }
        if (typeDeclaration instanceof JavassistClassDeclaration) {
          return ((JavassistClassDeclaration) typeDeclaration).solveMethod(name, argumentsTypes, staticOnly);
        }
          if (typeDeclaration instanceof JavassistEnumDeclaration) {
            return ((JavassistEnumDeclaration) typeDeclaration).solveMethod(name, argumentsTypes, staticOnly);
        }
        throw new UnsupportedOperationException(typeDeclaration.getClass().getCanonicalName());
    }

    ///
    /// Package-protected methods
    ///

    // USED ONLY IN DEPRECATED METHODS
    @Deprecated
    static boolean isApplicable(MethodUsage method, String name, List<Type> argumentsTypes,
                                TypeSolver typeSolver) {
        if (!method.getName().equals(name)) {
            return false;
        }
        // TODO Consider varargs
        if (method.getNoParams() != argumentsTypes.size()) {
            return false;
        }
        for (int i = 0; i < method.getNoParams(); i++) {
            Type expectedType = method.getParamType(i);
            Type expectedTypeWithoutSubstitutions = expectedType;
            Type expectedTypeWithInference = method.getParamType(i);
            Type actualType = argumentsTypes.get(i);

            List<TypeParameterDeclaration> typeParameters = method.getDeclaration().getTypeParameters();
            typeParameters.addAll(method.declaringType().getTypeParameters());

            if (expectedType.describe().equals(actualType.describe())){
                return true;
            }

            Map<TypeParameterDeclaration, Type> derivedValues = new HashMap<>();
            for (int j = 0; j < method.getParamTypes().size(); j++) {
                ParameterDeclaration parameter = method.getDeclaration().getParam(i);
                Type parameterType = parameter.getType();
                if (parameter.isVariadic()) {
                    parameterType = parameterType.asArrayType().getComponentType();
                }
                inferTypes(argumentsTypes.get(j), parameterType, derivedValues);
            }

            for (Map.Entry<TypeParameterDeclaration, Type> entry : derivedValues.entrySet()){
                TypeParameterDeclaration tp = entry.getKey();
                expectedTypeWithInference = expectedTypeWithInference.replaceTypeVariables(tp, entry.getValue());
            }

            for (TypeParameterDeclaration tp : typeParameters) {
                if (tp.getBounds(typeSolver).isEmpty()) {
                    //expectedType = expectedType.replaceTypeVariables(tp.getName(), new ReferenceTypeUsageImpl(typeSolver.solveType(Object.class.getCanonicalName()), typeSolver));
                    expectedType = expectedType.replaceTypeVariables(tp, Wildcard.extendsBound(new ReferenceTypeImpl(typeSolver.solveType(Object.class.getCanonicalName()), typeSolver)));
                } else if (tp.getBounds(typeSolver).size() == 1) {
                    TypeParameterDeclaration.Bound bound = tp.getBounds(typeSolver).get(0);
                    if (bound.isExtends()) {
                        //expectedType = expectedType.replaceTypeVariables(tp.getName(), bound.getType());
                        expectedType = expectedType.replaceTypeVariables(tp, Wildcard.extendsBound(bound.getType()));
                    } else {
                        //expectedType = expectedType.replaceTypeVariables(tp.getName(), new ReferenceTypeUsageImpl(typeSolver.solveType(Object.class.getCanonicalName()), typeSolver));
                        expectedType = expectedType.replaceTypeVariables(tp, Wildcard.superBound(bound.getType()));
                    }
                } else {
                    throw new UnsupportedOperationException();
                }
            }
            Type expectedType2 = expectedTypeWithoutSubstitutions;
            for (TypeParameterDeclaration tp : typeParameters) {
                if (tp.getBounds(typeSolver).isEmpty()) {
                    expectedType2 = expectedType2.replaceTypeVariables(tp, new ReferenceTypeImpl(typeSolver.solveType(Object.class.getCanonicalName()), typeSolver));
                } else if (tp.getBounds(typeSolver).size() == 1) {
                    TypeParameterDeclaration.Bound bound = tp.getBounds(typeSolver).get(0);
                    if (bound.isExtends()) {
                        expectedType2 = expectedType2.replaceTypeVariables(tp, bound.getType());
                    } else {
                        expectedType2 = expectedType2.replaceTypeVariables(tp, new ReferenceTypeImpl(typeSolver.solveType(Object.class.getCanonicalName()), typeSolver));
                    }
                } else {
                    throw new UnsupportedOperationException();
                }
            }
            if (!expectedType.isAssignableBy(actualType)
                    && !expectedType2.isAssignableBy(actualType)
                    && !expectedTypeWithInference.isAssignableBy(actualType)
                    && !expectedTypeWithoutSubstitutions.isAssignableBy(actualType)) {
                return false;
            }
        }
        return true;
    }

    static boolean isApplicable(MethodDeclaration method, MethodCallExpr methodCallExpr,
                                TypeSolver typeSolver) {
        TypeInference typeInference = new TypeInference(typeSolver);
        return typeInference.invocationApplicabilityInference(methodCallExpr, method);
    }


    static boolean isAssignableMatchTypeParameters(ReferenceType expected, ReferenceType actual,
                                                   Map<String, Type> matchedParameters) {
        if (actual.getQualifiedName().equals(expected.getQualifiedName())) {
            return isAssignableMatchTypeParametersMatchingQName(expected, actual, matchedParameters);
        } else {
            List<ReferenceType> ancestors = actual.getAllAncestors();
            for (ReferenceType ancestor : ancestors) {
                if (isAssignableMatchTypeParametersMatchingQName(expected, ancestor, matchedParameters)) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean isExactMatch(MethodLikeDeclaration method, List<Type> argumentsTypes) {
        for (int i = 0; i < method.getNumberOfParams(); i++) {
            if (!method.getParam(i).getType().equals(argumentsTypes.get(i))) {
                return false;
            }
        }
        return true;
    }

    ///
    /// Private methods
    ///

    private static List<Type> groupVariadicParamValues(List<Type> argumentsTypes, int startVariadic,
                                                       Type variadicType) {
        List<Type> res = new ArrayList<>(argumentsTypes.subList(0, startVariadic));
        List<Type> variadicValues = argumentsTypes.subList(startVariadic, argumentsTypes.size());
        if (variadicValues.isEmpty()) {
            // TODO if there are no variadic values we should default to the bound of the formal type
            res.add(variadicType);
        } else {
            Type componentType = findCommonType(variadicValues);
            res.add(new ArrayType(componentType));
        }
        return res;
    }

    private static Type findCommonType(List<Type> variadicValues) {
        if (variadicValues.isEmpty()) {
            throw new IllegalArgumentException();
        }
        // TODO implement this decently
        return variadicValues.get(0);
    }

    // USED ONLY IN DEPRECATED METHODS
    @Deprecated
    private static boolean isApplicable(MethodDeclaration method, String name, List<Type> argumentsTypes,
                                        TypeSolver typeSolver, boolean withWildcardTolerance) {
        if (!method.getName().equals(name)) {
            return false;
        }
        if (method.hasVariadicParameter()) {
            int pos = method.getNumberOfParams() - 1;
            if (method.getNumberOfParams() == argumentsTypes.size()) {
                // check if the last value is directly assignable as an array
                Type expectedType = method.getLastParam().getType();
                Type actualType = argumentsTypes.get(pos);
                if (!expectedType.isAssignableBy(actualType)) {
                    for (TypeParameterDeclaration tp : method.getTypeParameters()) {
                        expectedType = replaceTypeParam(expectedType, tp, typeSolver);
                    }
                    if (!expectedType.isAssignableBy(actualType)) {
                        if (actualType.isArray() && expectedType.isAssignableBy(actualType.asArrayType().getComponentType())) {
                            argumentsTypes.set(pos, actualType.asArrayType().getComponentType());
                        } else {
                            argumentsTypes = groupVariadicParamValues(argumentsTypes, pos, method.getLastParam().getType());
                        }
                    }
                } // else it is already assignable, nothing to do
            } else {
                if (pos > argumentsTypes.size()) {
                    return false;
                }
                argumentsTypes = groupVariadicParamValues(argumentsTypes, pos, method.getLastParam().getType());
            }
        }

        if (method.getNumberOfParams() != argumentsTypes.size()) {
            return false;
        }
        Map<String, Type> matchedParameters = new HashMap<>();
        boolean needForWildCardTolerance = false;
        for (int i = 0; i < method.getNumberOfParams(); i++) {
            Type expectedType = method.getParam(i).getType();
            Type actualType = argumentsTypes.get(i);
            if ((expectedType.isTypeVariable() && !(expectedType.isWildcard())) && expectedType.asTypeParameter().declaredOnMethod()) {
                matchedParameters.put(expectedType.asTypeParameter().getName(), actualType);
                continue;
            }
            boolean isAssignableWithoutSubstitution = expectedType.isAssignableBy(actualType) ||
                    (method.getParam(i).isVariadic() && new ArrayType(expectedType).isAssignableBy(actualType));
            if (!isAssignableWithoutSubstitution && expectedType.isReferenceType() && actualType.isReferenceType()) {
                isAssignableWithoutSubstitution = isAssignableMatchTypeParameters(
                        expectedType.asReferenceType(),
                        actualType.asReferenceType(),
                        matchedParameters);
            }
            if (!isAssignableWithoutSubstitution) {
                List<TypeParameterDeclaration> typeParameters = method.getTypeParameters();
                typeParameters.addAll(method.declaringType().getTypeParameters());
                for (TypeParameterDeclaration tp : typeParameters) {
                    expectedType = replaceTypeParam(expectedType, tp, typeSolver);
                }

                if (!expectedType.isAssignableBy(actualType)) {
                    if (actualType.isWildcard() && withWildcardTolerance && !expectedType.isPrimitive()) {
                        needForWildCardTolerance = true;
                        continue;
                    }
                    if (method.hasVariadicParameter() && i == method.getNumberOfParams() - 1) {
                        if (new ArrayType(expectedType).isAssignableBy(actualType)) {
                            continue;
                        }
                    }
                    return false;
                }
            }
        }
        return !withWildcardTolerance || needForWildCardTolerance;
    }


    private static boolean isAssignableMatchTypeParameters(Type expected, Type actual,
                                                           Map<String, Type> matchedParameters) {
        if (expected.isReferenceType() && actual.isReferenceType()) {
            return isAssignableMatchTypeParameters(expected.asReferenceType(), actual.asReferenceType(), matchedParameters);
        } else if (expected.isTypeVariable()) {
            matchedParameters.put(expected.asTypeParameter().getName(), actual);
            return true;
        } else {
            throw new UnsupportedOperationException(expected.getClass().getCanonicalName() + " " + actual.getClass().getCanonicalName());
        }
    }


    private static boolean isAssignableMatchTypeParametersMatchingQName(ReferenceType expected, ReferenceType actual,
                                                                        Map<String, Type> matchedParameters) {

        if (!expected.getQualifiedName().equals(actual.getQualifiedName())) {
            return false;
        }
        if (expected.typeParametersValues().size() != actual.typeParametersValues().size()) {
            throw new UnsupportedOperationException();
            //return true;
        }
        for (int i = 0; i < expected.typeParametersValues().size(); i++) {
            Type expectedParam = expected.typeParametersValues().get(i);
            Type actualParam = actual.typeParametersValues().get(i);

            // In the case of nested parameterizations eg. List<R> <-> List<Integer>
            // we should peel off one layer and ensure R <-> Integer
            if (expectedParam.isReferenceType() && actualParam.isReferenceType()){
                ReferenceType r1 = expectedParam.asReferenceType();
                ReferenceType r2 = actualParam.asReferenceType();

                return isAssignableMatchTypeParametersMatchingQName(r1, r2, matchedParameters);
            }

            if (expectedParam.isTypeVariable()) {
                String expectedParamName = expectedParam.asTypeParameter().getName();
                if (!actualParam.isTypeVariable() || !actualParam.asTypeParameter().getName().equals(expectedParamName)) {
                    if (matchedParameters.containsKey(expectedParamName)) {
                        Type matchedParameter = matchedParameters.get(expectedParamName);
                        if (matchedParameter.isAssignableBy(actualParam)) {
                            return true;
                        } else if (actualParam.isAssignableBy(matchedParameter)) {
                            matchedParameters.put(expectedParamName, actualParam);
                            return true;
                        }
                        return false;
                    } else {
                        matchedParameters.put(expectedParamName, actualParam);
                    }
                }
            } else if (expectedParam.isReferenceType()) {
                if (!expectedParam.equals(actualParam)) {
                    return false;
                }
            } else if (expectedParam.isWildcard()) {
                if (expectedParam.asWildcard().isExtends()) {
                    return isAssignableMatchTypeParameters(expectedParam.asWildcard().getBoundedType(), actual, matchedParameters);
                }
                // TODO verify super bound
                return true;
            } else {
                throw new UnsupportedOperationException(expectedParam.describe());
            }
        }
        return true;
    }


    private static List<MethodDeclaration> getMethodsWithoutDuplicates(List<MethodDeclaration> methods) {
        Set<MethodDeclaration> s = new TreeSet<MethodDeclaration>(new Comparator<MethodDeclaration>() {
            @Override
            public int compare(MethodDeclaration m1, MethodDeclaration m2) {
                if (m1 instanceof JavaParserMethodDeclaration && m2 instanceof JavaParserMethodDeclaration &&
                        ((JavaParserMethodDeclaration) m1).getWrappedNode().equals(((JavaParserMethodDeclaration) m2).getWrappedNode())) {
                    return 0;
                }
                return 1;
            }
        });
        s.addAll(methods);
        List<MethodDeclaration> res = new ArrayList<>();
        Set<String> usedSignatures = new HashSet<>();
        for (MethodDeclaration md : methods) {
            String signature = md.getQualifiedSignature();
            if (!usedSignatures.contains(signature)) {
                usedSignatures.add(signature);
                res.add(md);
            }
        }
        return res;
    }

    private static boolean isMoreSpecific(MethodDeclaration methodA, MethodDeclaration methodB,
                                          List<Type> argumentTypes, TypeSolver typeSolver) {
        boolean oneMoreSpecificFound = false;
        if (methodA.getNumberOfParams() < methodB.getNumberOfParams()) {
            return true;
        }
        if (methodA.getNumberOfParams() > methodB.getNumberOfParams()) {
            return false;
        }
        for (int i = 0; i < methodA.getNumberOfParams(); i++) {
            Type tdA = methodA.getParam(i).getType();
            Type tdB = methodB.getParam(i).getType();
            // B is more specific
            if (tdB.isAssignableBy(tdA) && !tdA.isAssignableBy(tdB)) {
                oneMoreSpecificFound = true;
            }
            // A is more specific
            if (tdA.isAssignableBy(tdB) && !tdB.isAssignableBy(tdA)) {
                return false;
            }
        }

        if (!oneMoreSpecificFound) {
            int lastIndex = argumentTypes.size() - 1;

            if (methodA.hasVariadicParameter() && !methodB.hasVariadicParameter()) {
                // if the last argument is an array then m1 is more specific
                if (argumentTypes.get(lastIndex).isArray()) {
                    return true;
                }

                if (!argumentTypes.get(lastIndex).isArray()) {
                    return false;
                }
            }
            if (!methodA.hasVariadicParameter() && methodB.hasVariadicParameter()) {
                // if the last argument is an array and m1 is not variadic then
                // it is not more specific
                if (argumentTypes.get(lastIndex).isArray()) {
                    return false;
                }

                if (!argumentTypes.get(lastIndex).isArray()) {
                    return true;
                }
            }
        }

        return oneMoreSpecificFound;
    }

    private static boolean isMoreSpecific(MethodUsage methodA, MethodUsage methodB, TypeSolver typeSolver) {
        boolean oneMoreSpecificFound = false;
        for (int i = 0; i < methodA.getNoParams(); i++) {
            Type tdA = methodA.getParamType(i);
            Type tdB = methodB.getParamType(i);

            boolean aIsAssignableByB = tdA.isAssignableBy(tdB);
            boolean bIsAssignableByA = tdB.isAssignableBy(tdA);

            // B is more specific
            if (bIsAssignableByA && !aIsAssignableByB) {
                oneMoreSpecificFound = true;
            }
            // A is more specific
            if (aIsAssignableByB && !bIsAssignableByA) {
                return false;
            }
        }
        return oneMoreSpecificFound;
    }


    private static boolean areOverride(MethodUsage winningCandidate, MethodUsage other) {
        if (!winningCandidate.getName().equals(other.getName())) {
            return false;
        }
        if (winningCandidate.getNoParams() != other.getNoParams()) {
            return false;
        }
        for (int i = 0; i < winningCandidate.getNoParams(); i++) {
            if (!winningCandidate.getParamTypes().get(i).equals(other.getParamTypes().get(i))) {
                return false;
            }
        }
        return true;
    }


    // USED ONLY IN DEPRECATED METHODS
    @Deprecated
    private static void inferTypes(Type source, Type target, Map<TypeParameterDeclaration, Type> mappings) {
        if (source.equals(target)) {
            return;
        }
        if (source.isReferenceType() && target.isReferenceType()) {
            ReferenceType sourceRefType = source.asReferenceType();
            ReferenceType targetRefType = target.asReferenceType();
            if (sourceRefType.getQualifiedName().equals(targetRefType.getQualifiedName())) {
                if (!sourceRefType.isRawType() && !targetRefType.isRawType()) {
                    for (int i = 0; i < sourceRefType.typeParametersValues().size(); i++) {
                        inferTypes(sourceRefType.typeParametersValues().get(i), targetRefType.typeParametersValues().get(i), mappings);
                    }
                }
            }
            return;
        }
        if (source.isReferenceType() && target.isWildcard()) {
            if (target.asWildcard().isBounded()) {
                inferTypes(source, target.asWildcard().getBoundedType(), mappings);
                return;
            }
            return;
        }
        if (source.isWildcard() && target.isWildcard()) {
            return;
        }
        if (source.isReferenceType() && target.isTypeVariable()) {
            mappings.put(target.asTypeParameter(), source);
            return;
        }

        if (source.isWildcard() && target.isReferenceType()){
            if (source.asWildcard().isBounded()){
                inferTypes(source.asWildcard().getBoundedType(), target, mappings);
            }
            return;
        }

        if (source.isWildcard() && target.isTypeVariable()) {
            mappings.put(target.asTypeParameter(), source);
            return;
        }
        if (source.isTypeVariable() && target.isTypeVariable()) {
            mappings.put(target.asTypeParameter(), source);
            return;
        }
        if (source.isPrimitive() || target.isPrimitive()) {
            return;
        }
        if (source.isNull()) {
            return;
        }
    }

}

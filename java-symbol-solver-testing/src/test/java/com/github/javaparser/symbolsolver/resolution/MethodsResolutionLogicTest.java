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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFactory;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserClassDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.model.methods.MethodUsage;
import com.github.javaparser.symbolsolver.model.typesystem.ReferenceType;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionFactory;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MethodsResolutionLogicTest extends AbstractResolutionTest {

    private TypeSolver typeSolver;

    @Before
    public void setup() {
        File srcNewCode = adaptPath(new File("src/test/resources/javaparser_new_src/javaparser-core"));
        CombinedTypeSolver combinedTypeSolverNewCode = new CombinedTypeSolver();
        combinedTypeSolverNewCode.add(new ReflectionTypeSolver());
        combinedTypeSolverNewCode.add(new JavaParserTypeSolver(srcNewCode));
        combinedTypeSolverNewCode.add(new JavaParserTypeSolver(adaptPath(new File("src/test/resources/javaparser_new_src/javaparser-generated-sources"))));
        typeSolver = combinedTypeSolverNewCode;
    }

    @Test
    public void anonymousClassAsMethodArgumentUsingTypeInference() throws Exception {
        CompilationUnit cu = parseSample("AnonymousClassDeclarations");
        ClassOrInterfaceDeclaration aClass = Navigator.demandClass(cu, "AnonymousClassDeclarations");
        MethodDeclaration method = Navigator.demandMethod(aClass, "fooBar1");
        MethodCallExpr methodCall = Navigator.findMethodCall(method, "of");

        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());

        MethodDeclaration jpMethodDeclaration = Navigator.demandClass(cu, "AnonymousClassDeclarations.ParDo").getMethodsByName("of").get(0);
        com.github.javaparser.symbolsolver.model.declarations.MethodDeclaration methodDeclaration =
                new JavaParserMethodDeclaration(jpMethodDeclaration, combinedTypeSolver);
        assertEquals(true, MethodResolutionLogic.isApplicable(methodDeclaration, methodCall, combinedTypeSolver));
    }

    @Test
    public void compatibilityShouldConsiderAlsoTypeVariablesNegative() {
        JavaParserClassDeclaration constructorDeclaration = (JavaParserClassDeclaration) typeSolver.solveType("com.github.javaparser.ast.body.ConstructorDeclaration");

        ReferenceType stringType = (ReferenceType) ReflectionFactory.typeUsageFor(String.class, typeSolver);
        ReferenceType rawClassType = (ReferenceType) ReflectionFactory.typeUsageFor(Class.class, typeSolver);
        assertEquals(true, rawClassType.isRawType());
        ReferenceType classOfStringType = (ReferenceType) rawClassType.replaceTypeVariables(rawClassType.getTypeDeclaration().getTypeParameters().get(0), stringType);
        MethodUsage mu = constructorDeclaration.getAllMethods().stream().filter(m -> m.getDeclaration().getSignature().equals("isThrows(java.lang.Class<? extends java.lang.Throwable>)")).findFirst().get();

        assertEquals(false, MethodResolutionLogic.isApplicable(mu, "isThrows", ImmutableList.of(classOfStringType), typeSolver));
    }

    @Test
    public void compatibilityShouldConsiderAlsoTypeVariablesRaw() {
        JavaParserClassDeclaration constructorDeclaration = (JavaParserClassDeclaration) typeSolver.solveType("com.github.javaparser.ast.body.ConstructorDeclaration");

        ReferenceType rawClassType = (ReferenceType) ReflectionFactory.typeUsageFor(Class.class, typeSolver);
        MethodUsage mu = constructorDeclaration.getAllMethods().stream().filter(m -> m.getDeclaration().getSignature().equals("isThrows(java.lang.Class<? extends java.lang.Throwable>)")).findFirst().get();

        assertEquals(true, MethodResolutionLogic.isApplicable(mu, "isThrows", ImmutableList.of(rawClassType), typeSolver));
    }

    @Test
    public void compatibilityShouldConsiderAlsoTypeVariablesPositive() {
        JavaParserClassDeclaration constructorDeclaration = (JavaParserClassDeclaration) typeSolver.solveType("com.github.javaparser.ast.body.ConstructorDeclaration");

        ReferenceType runtimeException = (ReferenceType) ReflectionFactory.typeUsageFor(RuntimeException.class, typeSolver);
        ReferenceType rawClassType = (ReferenceType) ReflectionFactory.typeUsageFor(Class.class, typeSolver);
        ReferenceType classOfRuntimeType = (ReferenceType) rawClassType.replaceTypeVariables(rawClassType.getTypeDeclaration().getTypeParameters().get(0), runtimeException);
        MethodUsage mu = constructorDeclaration.getAllMethods().stream().filter(m -> m.getDeclaration().getSignature().equals("isThrows(java.lang.Class<? extends java.lang.Throwable>)")).findFirst().get();

        assertEquals(true, MethodResolutionLogic.isApplicable(mu, "isThrows", ImmutableList.of(classOfRuntimeType), typeSolver));
    }
}

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

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserClassDeclaration;
import com.github.javaparser.symbolsolver.model.declarations.MethodDeclaration;
import com.github.javaparser.symbolsolver.model.declarations.ReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.model.methods.MethodUsage;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.model.typesystem.ReferenceType;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionFactory;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class MethodsResolutionLogicUsingTypeInferenceTest extends AbstractResolutionTest {

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
    public void compatibilityFor() {
        CompilationUnit cu = JavaParser.parse("import java.util.LinkedList;\n" +
                "import java.util.Collections;\n" +
                "\n" +
                "class A {\n" +
                "\tObject field = Collections.disjoint(new LinkedList<String>(), new LinkedList<String>());\t\n" +
                "}");

        ReferenceTypeDeclaration typeDeclaration = typeSolver.solveType(Collections.class.getCanonicalName());
        MethodDeclaration methodDeclaration = typeDeclaration.getDeclaredMethods().stream().filter(m -> m.getName().equals("disjoint")).findFirst().get();
        MethodCallExpr methodCallExpr = (MethodCallExpr) cu.getClassByName("A").get().getFields().get(0).getVariables().get(0).getInitializer().get();

        assertEquals(true, MethodResolutionLogic.isApplicable(methodDeclaration, methodCallExpr, typeSolver));
    }

    @Test
    public void compatibilityShouldConsiderAlsoTypeVariablesNegative() {
        JavaParserClassDeclaration constructorDeclaration = (JavaParserClassDeclaration) typeSolver
                .solveType("com.github.javaparser.ast.body.ConstructorDeclaration");

        ReferenceType stringType = (ReferenceType) ReflectionFactory.typeUsageFor(String.class, typeSolver);
        ReferenceType rawClassType = (ReferenceType) ReflectionFactory.typeUsageFor(Class.class, typeSolver);
        assertEquals(true, rawClassType.isRawType());
        ReferenceType classOfStringType = (ReferenceType) rawClassType
                .replaceTypeVariables(rawClassType.getTypeDeclaration().getTypeParameters().get(0), stringType);
        MethodUsage mu = constructorDeclaration.getAllMethods().stream()
                .filter(m -> m.getDeclaration().getSignature()
                        .equals("isThrows(java.lang.Class<? extends java.lang.Throwable>)"))
                .findFirst().get();

        MethodCallExpr methodCallExpr = new MethodCallExpr(null, "isThrows");
        methodCallExpr.addArgument("\"AString\"");

        assertEquals(false,
                MethodResolutionLogic.isApplicable(mu.getDeclaration(), methodCallExpr, typeSolver));
    }

}

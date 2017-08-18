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

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.methods.MethodUsage;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MethodsResolutionTest extends AbstractResolutionTest {

    @Test
    public void solveMethodAccessThroughSuper() throws ParseException {
        CompilationUnit cu = parseSample("AccessThroughSuper");
        com.github.javaparser.ast.body.ClassOrInterfaceDeclaration clazz = Navigator.demandClass(cu, "AccessThroughSuper.SubClass");
        MethodDeclaration method = Navigator.demandMethod(clazz, "methodTest");
        ReturnStmt returnStmt = (ReturnStmt) method.getBody().get().getStatements().get(0);
        Expression expression = returnStmt.getExpression().get();

        Type ref = JavaParserFacade.get(new ReflectionTypeSolver()).getType(expression);
        assertEquals("java.lang.String", ref.describe());
    }

    @Test
    public void solveMethodWithClassExpressionAsParameter() throws ParseException {
        CompilationUnit cu = parseSample("ClassExpression");
        com.github.javaparser.ast.body.ClassOrInterfaceDeclaration clazz = Navigator.demandClass(cu, "ClassExpression");
        MethodDeclaration method = Navigator.demandMethod(clazz, "foo");
        MethodCallExpr expression = Navigator.findMethodCall(method, "noneOf");

        MethodUsage methodUsage = JavaParserFacade.get(new ReflectionTypeSolver()).solveMethodAsUsage(expression);
        assertEquals("noneOf", methodUsage.getName());
    }

    @Test
    public void solveMethodWithClassExpressionAsParameterUsingTypeInference() throws ParseException {
        CompilationUnit cu = parseSample("ClassExpression");
        com.github.javaparser.ast.body.ClassOrInterfaceDeclaration clazz = Navigator.demandClass(cu, "ClassExpression");
        MethodDeclaration method = Navigator.demandMethod(clazz, "foo");
        MethodCallExpr expression = Navigator.findMethodCall(method, "noneOf");

        MethodUsage methodUsage = JavaParserFacade.get(new ReflectionTypeSolver()).solveMethodAsUsageUsingTypeInference(expression);
        assertEquals("noneOf", methodUsage.getName());
    }

    @Test
    public void solveMethodInInterfaceParent() throws ParseException {
        CompilationUnit cu = parseSample("MethodCalls");
        ClassOrInterfaceDeclaration clazz = Navigator.demandClass(cu, "MethodCalls");

        MethodDeclaration method = Navigator.demandMethod(clazz, "inheritedInterfaceMethod");
        MethodCallExpr expression = Navigator.findMethodCall(method, "toString");

        TypeSolver typeSolver = new ReflectionTypeSolver();

        JavaParserFacade javaParserFacade = JavaParserFacade.get(typeSolver);
        MethodUsage call1 = javaParserFacade.solveMethodAsUsage(expression);
        assertEquals("java.lang.Object.toString()", call1.getQualifiedSignature());
    }

    @Test
    public void solveMethodInInterfaceParentUsingTypeInference() throws ParseException {
        CompilationUnit cu = parseSample("MethodCalls");
        ClassOrInterfaceDeclaration clazz = Navigator.demandClass(cu, "MethodCalls");

        MethodDeclaration method = Navigator.demandMethod(clazz, "inheritedInterfaceMethod");
        MethodCallExpr expression = Navigator.findMethodCall(method, "toString");

        TypeSolver typeSolver = new ReflectionTypeSolver();

        JavaParserFacade javaParserFacade = JavaParserFacade.get(typeSolver);
        MethodUsage call1 = javaParserFacade.solveMethodAsUsageUsingTypeInference(expression);
        assertEquals("java.lang.Object.toString()", call1.getQualifiedSignature());
    }
}

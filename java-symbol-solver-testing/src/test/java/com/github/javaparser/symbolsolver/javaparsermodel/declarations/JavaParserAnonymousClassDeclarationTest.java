package com.github.javaparser.symbolsolver.javaparsermodel.declarations;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.methods.MethodUsage;
import com.github.javaparser.symbolsolver.resolution.AbstractResolutionTest;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.junit.Test;


public class JavaParserAnonymousClassDeclarationTest extends AbstractResolutionTest {

  @Test
  @Deprecated
  public void anonymousClassAsMethodArgument() throws Exception {
    CompilationUnit cu = parseSample("AnonymousClassDeclarations");
    ClassOrInterfaceDeclaration aClass = Navigator.demandClass(cu, "AnonymousClassDeclarations");
    MethodDeclaration method = Navigator.demandMethod(aClass, "fooBar1");
    MethodCallExpr methodCall = Navigator.findMethodCall(method, "of");

    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(new ReflectionTypeSolver());
    MethodUsage methodUsage =
        JavaParserFacade.get(combinedTypeSolver).solveMethodAsUsage(methodCall);

    assertThat(methodUsage.getQualifiedSignature(),
               is("AnonymousClassDeclarations.ParDo.of(AnonymousClassDeclarations.DoFn<I, O>)"));
  }

  @Test
  public void anonymousClassAsMethodArgumentUsingTypeInference() throws Exception {
    CompilationUnit cu = parseSample("AnonymousClassDeclarations");
    ClassOrInterfaceDeclaration aClass = Navigator.demandClass(cu, "AnonymousClassDeclarations");
    MethodDeclaration method = Navigator.demandMethod(aClass, "fooBar1");
    MethodCallExpr methodCall = Navigator.findMethodCall(method, "of");

    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(new ReflectionTypeSolver());
    MethodUsage methodUsage =
            JavaParserFacade.get(combinedTypeSolver).solveMethodAsUsageUsingTypeInference(methodCall);

    assertThat(methodUsage.getQualifiedSignature(),
            is("AnonymousClassDeclarations.ParDo.of(AnonymousClassDeclarations.DoFn<I, O>)"));
  }

  @Test
  public void callingSuperClassInnerClassMethod() throws Exception {
    CompilationUnit cu = parseSample("AnonymousClassDeclarations");
    ClassOrInterfaceDeclaration aClass = Navigator.demandClass(cu, "AnonymousClassDeclarations");
    MethodDeclaration method = Navigator.demandMethod(aClass, "fooBar2");
    MethodCallExpr methodCall = Navigator.findMethodCall(method, "innerClassMethod");

    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(new ReflectionTypeSolver());
    MethodUsage methodUsage =
        JavaParserFacade.get(combinedTypeSolver).solveMethodAsUsage(methodCall);

    assertThat(methodUsage.getQualifiedSignature(),
               is("AnonymousClassDeclarations.DoFn.ProcessContext.innerClassMethod()"));
  }

  @Test
  public void callingSuperClassInnerClassMethodUsingTypeInference() throws Exception {
    CompilationUnit cu = parseSample("AnonymousClassDeclarations");
    ClassOrInterfaceDeclaration aClass = Navigator.demandClass(cu, "AnonymousClassDeclarations");
    MethodDeclaration method = Navigator.demandMethod(aClass, "fooBar2");
    MethodCallExpr methodCall = Navigator.findMethodCall(method, "innerClassMethod");

    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(new ReflectionTypeSolver());
    MethodUsage methodUsage =
            JavaParserFacade.get(combinedTypeSolver).solveMethodAsUsageUsingTypeInference(methodCall);

    assertThat(methodUsage.getQualifiedSignature(),
            is("AnonymousClassDeclarations.DoFn.ProcessContext.innerClassMethod()"));
  }

  @Test
  public void callingAnonymousClassInnerMethod() throws Exception {
    CompilationUnit cu = parseSample("AnonymousClassDeclarations");
    ClassOrInterfaceDeclaration aClass = Navigator.demandClass(cu, "AnonymousClassDeclarations");
    MethodDeclaration method = Navigator.demandMethod(aClass, "fooBar3");
    MethodCallExpr methodCall = Navigator.findMethodCall(method, "callAnnonClassInnerMethod");

    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(new ReflectionTypeSolver());
    MethodUsage methodUsage =
        JavaParserFacade.get(combinedTypeSolver).solveMethodAsUsage(methodCall);

    assertTrue(methodUsage.getQualifiedSignature().startsWith("AnonymousClassDeclarations"));
    assertTrue(methodUsage.getQualifiedSignature().contains("Anonymous-"));
    assertTrue(methodUsage.getQualifiedSignature().endsWith("callAnnonClassInnerMethod()"));
  }

  @Test
  public void callingAnonymousClassInnerMethodUsingTypeInference() throws Exception {
    CompilationUnit cu = parseSample("AnonymousClassDeclarations");
    ClassOrInterfaceDeclaration aClass = Navigator.demandClass(cu, "AnonymousClassDeclarations");
    MethodDeclaration method = Navigator.demandMethod(aClass, "fooBar3");
    MethodCallExpr methodCall = Navigator.findMethodCall(method, "callAnnonClassInnerMethod");

    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(new ReflectionTypeSolver());
    MethodUsage methodUsage =
            JavaParserFacade.get(combinedTypeSolver).solveMethodAsUsageUsingTypeInference(methodCall);

    assertTrue(methodUsage.getQualifiedSignature().startsWith("AnonymousClassDeclarations"));
    assertTrue(methodUsage.getQualifiedSignature().contains("Anonymous-"));
    assertTrue(methodUsage.getQualifiedSignature().endsWith("callAnnonClassInnerMethod()"));
  }

  @Test
  public void usingAnonymousSuperClassInnerType() throws Exception {
    CompilationUnit cu = parseSample("AnonymousClassDeclarations");
    ClassOrInterfaceDeclaration aClass = Navigator.demandClass(cu, "AnonymousClassDeclarations");
    MethodDeclaration method = Navigator.demandMethod(aClass, "fooBar4");
    MethodCallExpr methodCall = Navigator.findMethodCall(method, "toString");

    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(new ReflectionTypeSolver());
    MethodUsage methodUsage =
        JavaParserFacade.get(combinedTypeSolver).solveMethodAsUsage(methodCall);

    assertThat(methodUsage.getQualifiedSignature(), is("java.lang.Enum.toString()"));
  }

  @Test
  public void usingAnonymousSuperClassInnerTypeUsingTypeInference() throws Exception {
    CompilationUnit cu = parseSample("AnonymousClassDeclarations");
    ClassOrInterfaceDeclaration aClass = Navigator.demandClass(cu, "AnonymousClassDeclarations");
    MethodDeclaration method = Navigator.demandMethod(aClass, "fooBar4");
    MethodCallExpr methodCall = Navigator.findMethodCall(method, "toString");

    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(new ReflectionTypeSolver());
    MethodUsage methodUsage =
            JavaParserFacade.get(combinedTypeSolver).solveMethodAsUsageUsingTypeInference(methodCall);

    assertThat(methodUsage.getQualifiedSignature(), is("java.lang.Enum.toString()"));
  }

  @Test
  public void usingAnonymousClassInnerType() throws Exception {
    CompilationUnit cu = parseSample("AnonymousClassDeclarations");
    ClassOrInterfaceDeclaration aClass = Navigator.demandClass(cu, "AnonymousClassDeclarations");
    MethodDeclaration method = Navigator.demandMethod(aClass, "fooBar5");
    MethodCallExpr methodCall = Navigator.findMethodCall(method, "toString");

    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(new ReflectionTypeSolver());
    MethodUsage methodUsage =
        JavaParserFacade.get(combinedTypeSolver).solveMethodAsUsage(methodCall);

    assertThat(methodUsage.getQualifiedSignature(), is("java.lang.Enum.toString()"));
  }

  @Test
  public void usingAnonymousClassInnerTypeUsingTypeInference() throws Exception {
    CompilationUnit cu = parseSample("AnonymousClassDeclarations");
    ClassOrInterfaceDeclaration aClass = Navigator.demandClass(cu, "AnonymousClassDeclarations");
    MethodDeclaration method = Navigator.demandMethod(aClass, "fooBar5");
    MethodCallExpr methodCall = Navigator.findMethodCall(method, "toString");

    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(new ReflectionTypeSolver());
    MethodUsage methodUsage =
            JavaParserFacade.get(combinedTypeSolver).solveMethodAsUsageUsingTypeInference(methodCall);

    assertThat(methodUsage.getQualifiedSignature(), is("java.lang.Enum.toString()"));
  }
}

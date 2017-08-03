package com.github.javaparser.symbolsolver;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFactory;
import com.github.javaparser.symbolsolver.model.declarations.ClassDeclaration;
import com.github.javaparser.symbolsolver.model.declarations.MethodDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.AbstractResolutionTest;
import com.github.javaparser.symbolsolver.resolution.typesolvers.MemoryTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class Issue183 extends AbstractResolutionTest {

    private TypeSolver typeSolver;
    private JavaParserFacade javaParserFacade;

    @Before
    public void setup() {
        typeSolver = new ReflectionTypeSolver();
        javaParserFacade = JavaParserFacade.get(typeSolver);
    }

    @Test
    public void issue183sample1VerifyClasses() throws ParseException {
        CompilationUnit cu = parseSample("Issue183Sample1");
        ClassOrInterfaceDeclaration jpBaseCD = cu.getClassByName("Base").get();
        ClassOrInterfaceDeclaration jpDerivedCD = Navigator.findAllNodesOfGivenClass(jpBaseCD, ClassOrInterfaceDeclaration.class).get(0);
        ClassDeclaration baseCD = JavaParserFactory.toTypeDeclaration(jpBaseCD, typeSolver).asReferenceType().asClass();
        ClassDeclaration derivedCD = JavaParserFactory.toTypeDeclaration(jpDerivedCD, typeSolver).asReferenceType().asClass();
        assertEquals(true, derivedCD.isAssignableBy(baseCD));
    }

    @Test
    public void issue183sample1() throws ParseException {
        CompilationUnit cu = parseSample("Issue183Sample1");
        MethodCallExpr methodCall = Navigator.findMethodCall(cu, "bar");
        SymbolReference<MethodDeclaration> res =  javaParserFacade.solve(methodCall);
        assertEquals(true, res.isSolved());
    }

}

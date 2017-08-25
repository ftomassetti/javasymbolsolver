package com.github.javaparser.symbolsolver;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.methods.MethodUsage;
import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.AbstractResolutionTest;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.junit.Test;
import static org.junit.Assert.*;

public class Issue228 extends AbstractResolutionTest{

    @Test
    @Deprecated
    public void testSolvingMethodWitPrimitiveParameterTypeAsUsage() throws ParseException {
        String code = 
                  "class Test { "
                + "  long l = call(1); "
                + "  long call(final long i) { "
                + "    return i; "
                + "  }"
                + "}";
        CompilationUnit cu = JavaParser.parse(code);
        MethodCallExpr methodCall = cu.getChildNodesByType(MethodCallExpr.class).get(0);
        JavaParserFacade parserFacade = JavaParserFacade.get(new ReflectionTypeSolver());
        MethodUsage solvedCall = parserFacade.solveMethodAsUsage(methodCall);
        assertEquals("long", solvedCall.getParamType(0).describe());
    }

    @Test
    public void testSolvingMethodWitPrimitiveParameterTypeAsUsageUsingTypeInference() throws ParseException {
        String code =
                "class Test { "
                        + "  long l = call(1); "
                        + "  long call(final long i) { "
                        + "    return i; "
                        + "  }"
                        + "}";
        CompilationUnit cu = JavaParser.parse(code);
        MethodCallExpr methodCall = cu.getChildNodesByType(MethodCallExpr.class).get(0);
        JavaParserFacade parserFacade = JavaParserFacade.get(new ReflectionTypeSolver());
        MethodUsage solvedCall = parserFacade.solveMethodAsUsageUsingTypeInference(methodCall);
        assertEquals("long", solvedCall.getParamType(0).describe());
    }
}

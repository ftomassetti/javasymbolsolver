package com.github.javaparser.symbolsolver.resolution.typeinference;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.model.typesystem.Type;

import java.util.List;

/**
 * @author Federico Tomassetti
 */
public class ExpressionHelper {

    /**
     * See https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.2
     * @return
     */
    public static boolean isStandaloneExpression(Expression expression) {
        return !isPolyExpression(expression);
    }

    /**
     * See https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.2
     * @return
     */
    public static boolean isPolyExpression(Expression expression) {
        if (expression instanceof EnclosedExpr) {
            throw new UnsupportedOperationException(expression.toString());
        }
        if (expression instanceof ObjectCreationExpr) {
            // A class instance creation expression is a poly expression (§15.2) if it uses the diamond form for type
            // arguments to the class, and it appears in an assignment context or an invocation context (§5.2, §5.3).
            // Otherwise, it is a standalone expression.
            ObjectCreationExpr objectCreationExpr = (ObjectCreationExpr)expression;
            if (objectCreationExpr.isUsingDiamondOperator()) {
                throw new UnsupportedOperationException(expression.toString());
            } else {
                return false;
            }
        }
        if (expression instanceof MethodCallExpr) {
            // A method invocation expression is a poly expression if all of the following are true:
            //
            // 1. The invocation appears in an assignment context or an invocation context (§5.2, §5.3).

            boolean condition1;

            // 2. If the invocation is qualified (that is, any form of MethodInvocation except for the first), then
            //    the invocation elides TypeArguments to the left of the Identifier.

            boolean condition2;

            // 3. The method to be invoked, as determined by the following subsections, is generic (§8.4.4) and has a
            //    return type that mentions at least one of the method's type parameters.

            boolean condition3;

            // Otherwise, the method invocation expression is a standalone expression.

            if (true) throw new UnsupportedOperationException(expression.toString());
            return condition1 && condition2 && condition3;
        }
        if (expression instanceof MethodReferenceExpr) {
            throw new UnsupportedOperationException(expression.toString());
        }
        if (expression instanceof ConditionalExpr) {
            throw new UnsupportedOperationException(expression.toString());
        }
        if (expression instanceof LambdaExpr) {
            return true;
        }
        return false;
    }

    public static boolean isExplicitlyTyped(LambdaExpr lambdaExpr) {
        return lambdaExpr.getParameters().stream().allMatch(p -> !(p.getType() instanceof UnknownType));
    }

    public static List<Expression> getResultExpressions(BlockStmt blockStmt) {
        throw new UnsupportedOperationException();
    }

    public static boolean isCompatibleInAssignmentContext(Expression expression, Type type, TypeSolver typeSolver) {
        return type.isAssignableBy(JavaParserFacade.get(typeSolver).getType(expression));
    }
}

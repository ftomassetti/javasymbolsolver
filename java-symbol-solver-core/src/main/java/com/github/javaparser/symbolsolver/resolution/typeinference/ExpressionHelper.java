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
            throw new UnsupportedOperationException(expression.toString());
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

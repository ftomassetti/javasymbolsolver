package com.github.javaparser.symbolsolver.resolution.typeinference;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;

/**
 * Constraint formulas are assertions of compatibility or subtyping that may involve inference variables.
 */
public abstract class ConstraintFormula {

    public BoundSet reduce(BoundSet currentBoundSet) {
        throw new UnsupportedOperationException();
    }

    /**
     * An expression is compatible in a loose invocation context with type T
     */
    class ExpressionCompatibleWithType extends ConstraintFormula {
        private Expression expression;
        private TIType T;
    }

    /**
     * A type S is compatible in a loose invocation context with type T
     */
    class TypeCompatibleWithType extends ConstraintFormula {
        private TIType S;
        private TIType T;
    }

    /**
     * A reference type S is a subtype of a reference type T
     */
    class TypeSubtypeOfType extends ConstraintFormula {
        private TIType S;
        private TIType T;
    }

    /**
     * A type argument S is contained by a type argument T
     */
    class TypeContainedByType extends ConstraintFormula {
        private TIType S;
        private TIType T;
    }

    /**
     * A type S is the same as a type T (§4.3.4), or a type argument S is the same as type argument T
     */
    class TypeSameAsType extends ConstraintFormula {
        private TIType S;
        private TIType T;
    }

    /**
     * The checked exceptions thrown by the body of the LambdaExpression are declared by the throws clause of the
     * function type derived from T.
     */
    class LambdaThrowsCompatibleWithType extends ConstraintFormula {
        private LambdaExpr lambdaExpression;
        private TIType T;
    }

    /**
     * The checked exceptions thrown by the referenced method are declared by the throws clause of the function type
     * derived from T.
     */
    class MethodReferenceThrowsCompatibleWithType extends ConstraintFormula {
        private MethodReferenceExpr methodReference;
        private TIType T;
    }

}

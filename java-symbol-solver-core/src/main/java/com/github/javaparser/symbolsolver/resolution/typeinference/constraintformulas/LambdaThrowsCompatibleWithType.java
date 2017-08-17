package com.github.javaparser.symbolsolver.resolution.typeinference.constraintformulas;

import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typeinference.BoundSet;
import com.github.javaparser.symbolsolver.resolution.typeinference.ConstraintFormula;

/**
 * The checked exceptions thrown by the body of the LambdaExpression are declared by the throws clause of the
 * function type derived from T.
 */
public class LambdaThrowsCompatibleWithType extends ConstraintFormula {
    private LambdaExpr lambdaExpression;
    private Type T;

    @Override
    public ReductionResult reduce(BoundSet currentBoundSet) {
        // A constraint formula of the form ‹LambdaExpression →throws T› is reduced as follows:
        //
        // - If T is not a functional interface type (§9.8), the constraint reduces to false.
        //
        // - Otherwise, let the target function type for the lambda expression be determined as specified in §15.27.3. If no valid function type can be found, the constraint reduces to false.
        //
        // - Otherwise, if the lambda expression is implicitly typed, and one or more of the function type's parameter types is not a proper type, the constraint reduces to false.
        //
        //   This condition never arises in practice, due to the substitution applied to the target type in §18.5.2.
        //
        // - Otherwise, if the function type's return type is neither void nor a proper type, the constraint reduces to false.
        //
        //   This condition never arises in practice, due to the substitution applied to the target type in §18.5.2.
        //
        // - Otherwise, let E1, ..., En be the types in the function type's throws clause that are not proper types. If the lambda expression is implicitly typed, let its parameter types be the function type's parameter types. If the lambda body is a poly expression or a block containing a poly result expression, let the targeted return type be the function type's return type. Let X1, ..., Xm be the checked exception types that the lambda body can throw (§11.2). Then there are two cases:
        //
        //   - If n = 0 (the function type's throws clause consists only of proper types), then if there exists some i (1 ≤ i ≤ m) such that Xi is not a subtype of any proper type in the throws clause, the constraint reduces to false; otherwise, the constraint reduces to true.
        //
        //   - If n > 0, the constraint reduces to a set of subtyping constraints: for all i (1 ≤ i ≤ m), if Xi is not a subtype of any proper type in the throws clause, then the constraints include, for all j (1 ≤ j ≤ n), ‹Xi <: Ej›. In addition, for all j (1 ≤ j ≤ n), the constraint reduces to the bound throws Ej.
        throw new UnsupportedOperationException();
    }
}

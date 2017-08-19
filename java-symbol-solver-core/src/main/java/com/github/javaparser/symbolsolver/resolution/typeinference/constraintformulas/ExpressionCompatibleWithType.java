package com.github.javaparser.symbolsolver.resolution.typeinference.constraintformulas;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typeinference.BoundSet;
import com.github.javaparser.symbolsolver.resolution.typeinference.ConstraintFormula;

import static com.github.javaparser.symbolsolver.resolution.typeinference.ExpressionHelper.isPolyExpression;
import static com.github.javaparser.symbolsolver.resolution.typeinference.ExpressionHelper.isStandaloneExpression;
import static com.github.javaparser.symbolsolver.resolution.typeinference.TypeHelper.isCompatibleInALooseInvocationContext;
import static com.github.javaparser.symbolsolver.resolution.typeinference.TypeHelper.isProperType;

/**
 * An expression is compatible in a loose invocation context with type T
 *
 * @author Federico Tomassetti
 */
public class ExpressionCompatibleWithType extends ConstraintFormula {
    private TypeSolver typeSolver;
    private Expression expression;
    private Type T;

    public ExpressionCompatibleWithType(TypeSolver typeSolver, Expression expression, Type T) {
        this.typeSolver = typeSolver;
        this.expression = expression;
        this.T = T;
    }

    @Override
    public ReductionResult reduce(BoundSet currentBoundSet) {
        // If T is a proper type, the constraint reduces to true if the expression is compatible in a loose
        // invocation context with T (§5.3), and false otherwise.

        if (isProperType(T)) {
            if (isCompatibleInALooseInvocationContext(typeSolver, expression, T)) {
                return ReductionResult.trueResult();
            } else {
                return ReductionResult.falseResult();
            }
        }

        // Otherwise, if the expression is a standalone expression (§15.2) of type S, the constraint reduces
        // to ‹S → T›.

        if (isStandaloneExpression(expression)) {
            Type s = JavaParserFacade.get(typeSolver).getType(expression);
            return ReductionResult.empty().withConstraint(new TypeCompatibleWithType(typeSolver, s, T));
        }

        // Otherwise, the expression is a poly expression (§15.2). The result depends on the form of the expression:

        if (isPolyExpression(expression)) {

            // - If the expression is a parenthesized expression of the form ( Expression' ), the constraint reduces
            //   to ‹Expression' → T›.

            if (expression instanceof EnclosedExpr) {
                EnclosedExpr enclosedExpr = (EnclosedExpr)expression;
                return ReductionResult.oneConstraint(new ExpressionCompatibleWithType(typeSolver, enclosedExpr.getInner(), T));
            }

            // - If the expression is a class instance creation expression or a method invocation expression, the
            //   constraint reduces to the bound set B3 which would be used to determine the expression's invocation
            //   type when targeting T, as defined in §18.5.2. (For a class instance creation expression, the
            //   corresponding "method" used for inference is defined in §15.9.3).
            //
            //   This bound set may contain new inference variables, as well as dependencies between these new
            //   variables and the inference variables in T.

            if (expression instanceof ObjectCreationExpr) {
                throw new UnsupportedOperationException();
            }

            if (expression instanceof MethodCallExpr) {
                throw new UnsupportedOperationException();
            }

            // - If the expression is a conditional expression of the form e1 ? e2 : e3, the constraint reduces to two
            //   constraint formulas, ‹e2 → T› and ‹e3 → T›.

            if (expression instanceof ConditionalExpr) {
                ConditionalExpr conditionalExpr = (ConditionalExpr)expression;
                return ReductionResult.withConstraints(
                        new ExpressionCompatibleWithType(typeSolver, conditionalExpr.getThenExpr(), T),
                        new ExpressionCompatibleWithType(typeSolver, conditionalExpr.getElseExpr(), T));
            }

            // - If the expression is a lambda expression or a method reference expression, the result is specified
            //   below.

            // A constraint formula of the form ‹LambdaExpression → T›, where T mentions at least one inference variable, is reduced as follows:

            if (expression instanceof LambdaExpr) {

                // - If T is not a functional interface type (§9.8), the constraint reduces to false.
                //
                // - Otherwise, let T' be the ground target type derived from T, as specified in §15.27.3. If §18.5.3
                //   is used to derive a functional interface type which is parameterized, then the test that
                //   F<A'1, ..., A'm> is a subtype of F<A1, ..., Am> is not performed (instead, it is asserted with a
                //   constraint formula below). Let the target function type for the lambda expression be the
                //   function type of T'. Then:
                //
                //   - If no valid function type can be found, the constraint reduces to false.
                //
                //   - Otherwise, the congruence of LambdaExpression with the target function type is asserted as
                //     follows:
                //
                //     - If the number of lambda parameters differs from the number of parameter types of the function
                //       type, the constraint reduces to false.
                //
                //     - If the lambda expression is implicitly typed and one or more of the function type's parameter
                //       types is not a proper type, the constraint reduces to false.
                //
                //       This condition never arises in practice, due to the handling of implicitly typed lambda
                //       expressions in §18.5.1 and the substitution applied to the target type in §18.5.2.
                //
                //     - If the function type's result is void and the lambda body is neither a statement expression
                //       nor a void-compatible block, the constraint reduces to false.
                //
                //     - If the function type's result is not void and the lambda body is a block that is not
                //       value-compatible, the constraint reduces to false.
                //
                //     - Otherwise, the constraint reduces to all of the following constraint formulas:
                //
                //       - If the lambda parameters have explicitly declared types F1, ..., Fn and the function type
                //         has parameter types G1, ..., Gn, then i) for all i (1 ≤ i ≤ n), ‹Fi = Gi›, and ii) ‹T' <: T›.
                //
                //       - If the function type's return type is a (non-void) type R, assume the lambda's parameter
                //         types are the same as the function type's parameter types. Then:
                //
                //         - If R is a proper type, and if the lambda body or some result expression in the lambda body
                //           is not compatible in an assignment context with R, then false.
                //
                //         - Otherwise, if R is not a proper type, then where the lambda body has the form Expression,
                //           the constraint ‹Expression → R›; or where the lambda body is a block with result
                //           expressions e1, ..., em, for all i (1 ≤ i ≤ m), ‹ei → R›.

                throw new UnsupportedOperationException();
            }

            // A constraint formula of the form ‹MethodReference → T›, where T mentions at least one inference variable, is reduced as follows:

            if (expression instanceof MethodReferenceExpr) {

                // - If T is not a functional interface type, or if T is a functional interface type that does not have a function type (§9.9), the constraint reduces to false.
                //
                // - Otherwise, if there does not exist a potentially applicable method for the method reference when targeting T, the constraint reduces to false.
                //
                // - Otherwise, if the method reference is exact (§15.13.1), then let P1, ..., Pn be the parameter types of the function type of T, and let F1, ..., Fk be the parameter types of the potentially applicable method. The constraint reduces to a new set of constraints, as follows:
                //
                //   - In the special case where n = k+1, the parameter of type P1 is to act as the target reference of the invocation. The method reference expression necessarily has the form ReferenceType :: [TypeArguments] Identifier. The constraint reduces to ‹P1 <: ReferenceType› and, for all i (2 ≤ i ≤ n), ‹Pi → Fi-1›.
                //
                //     In all other cases, n = k, and the constraint reduces to, for all i (1 ≤ i ≤ n), ‹Pi → Fi›.
                //
                //   - If the function type's result is not void, let R be its return type. Then, if the result of the potentially applicable compile-time declaration is void, the constraint reduces to false. Otherwise, the constraint reduces to ‹R' → R›, where R' is the result of applying capture conversion (§5.1.10) to the return type of the potentially applicable compile-time declaration.
                //
                // - Otherwise, the method reference is inexact, and:
                //
                //   - If one or more of the function type's parameter types is not a proper type, the constraint reduces to false.
                //
                //     This condition never arises in practice, due to the handling of inexact method references in §18.5.1 and the substitution applied to the target type in §18.5.2.
                //
                //   - Otherwise, a search for a compile-time declaration is performed, as specified in §15.13.1. If there is no compile-time declaration for the method reference, the constraint reduces to false. Otherwise, there is a compile-time declaration, and:
                //
                //     - If the result of the function type is void, the constraint reduces to true.
                //
                //     - Otherwise, if the method reference expression elides TypeArguments, and the compile-time declaration is a generic method, and the return type of the compile-time declaration mentions at least one of the method's type parameters, then the constraint reduces to the bound set B3 which would be used to determine the method reference's invocation type when targeting the return type of the function type, as defined in §18.5.2. B3 may contain new inference variables, as well as dependencies between these new variables and the inference variables in T.
                //
                //     - Otherwise, let R be the return type of the function type, and let R' be the result of applying capture conversion (§5.1.10) to the return type of the invocation type (§15.12.2.6) of the compile-time declaration. If R' is void, the constraint reduces to false; otherwise, the constraint reduces to ‹R' → R›.

                throw new UnsupportedOperationException();
            }

            throw new RuntimeException("This should not happen");
        }

        throw new RuntimeException("This should not happen");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpressionCompatibleWithType that = (ExpressionCompatibleWithType) o;

        if (!typeSolver.equals(that.typeSolver)) return false;
        if (!expression.equals(that.expression)) return false;
        return T.equals(that.T);
    }

    @Override
    public int hashCode() {
        int result = typeSolver.hashCode();
        result = 31 * result + expression.hashCode();
        result = 31 * result + T.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ExpressionCompatibleWithType{" +
                "typeSolver=" + typeSolver +
                ", expression=" + expression +
                ", T=" + T +
                '}';
    }
}

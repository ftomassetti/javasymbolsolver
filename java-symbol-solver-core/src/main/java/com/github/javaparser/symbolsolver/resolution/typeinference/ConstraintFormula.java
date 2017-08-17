package com.github.javaparser.symbolsolver.resolution.typeinference;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.utils.Pair;

import java.util.List;

/**
 * Constraint formulas are assertions of compatibility or subtyping that may involve inference variables.
 */
public abstract class ConstraintFormula {

    /**
     * A formula is reduced to one or both of:
     * i) A bound or bound set, which is to be incorporated with the "current" bound set. Initially, the current bound
     *    set is empty.
     * ii) Further constraint formulas, which are to be reduced recursively.
     */
    public abstract Pair<BoundSet, List<ConstraintFormula>> reduce(BoundSet currentBoundSet);

    /**
     * An expression is compatible in a loose invocation context with type T
     */
    class ExpressionCompatibleWithType extends ConstraintFormula {
        private Expression expression;
        private TIType T;

        @Override
        public Pair<BoundSet, List<ConstraintFormula>> reduce(BoundSet currentBoundSet) {
            // If T is a proper type, the constraint reduces to true if the expression is compatible in a loose
            // invocation context with T (§5.3), and false otherwise.
            //
            // Otherwise, if the expression is a standalone expression (§15.2) of type S, the constraint reduces
            // to ‹S → T›.
            //
            // Otherwise, the expression is a poly expression (§15.2). The result depends on the form of the expression:
            //
            // - If the expression is a parenthesized expression of the form ( Expression' ), the constraint reduces
            //   to ‹Expression' → T›.
            //
            // - If the expression is a class instance creation expression or a method invocation expression, the
            //   constraint reduces to the bound set B3 which would be used to determine the expression's invocation
            //   type when targeting T, as defined in §18.5.2. (For a class instance creation expression, the
            //   corresponding "method" used for inference is defined in §15.9.3).
            //
            // - This bound set may contain new inference variables, as well as dependencies between these new
            //   variables and the inference variables in T.
            //
            // - If the expression is a conditional expression of the form e1 ? e2 : e3, the constraint reduces to two
            //   constraint formulas, ‹e2 → T› and ‹e3 → T›.
            //
            // - If the expression is a lambda expression or a method reference expression, the result is specified
            //   below.


            // A constraint formula of the form ‹LambdaExpression → T›, where T mentions at least one inference variable, is reduced as follows:
            //
            // - If T is not a functional interface type (§9.8), the constraint reduces to false.
            //
            // - Otherwise, let T' be the ground target type derived from T, as specified in §15.27.3. If §18.5.3 is used to derive a functional interface type which is parameterized, then the test that F<A'1, ..., A'm> is a subtype of F<A1, ..., Am> is not performed (instead, it is asserted with a constraint formula below). Let the target function type for the lambda expression be the function type of T'. Then:
            //
            //   - If no valid function type can be found, the constraint reduces to false.
            //
            //   - Otherwise, the congruence of LambdaExpression with the target function type is asserted as follows:
            //
            //     - If the number of lambda parameters differs from the number of parameter types of the function type, the constraint reduces to false.
            //
            //     - If the lambda expression is implicitly typed and one or more of the function type's parameter types is not a proper type, the constraint reduces to false.
            //
            //       This condition never arises in practice, due to the handling of implicitly typed lambda expressions in §18.5.1 and the substitution applied to the target type in §18.5.2.
            //
            //     - If the function type's result is void and the lambda body is neither a statement expression nor a void-compatible block, the constraint reduces to false.
            //
            //     - If the function type's result is not void and the lambda body is a block that is not value-compatible, the constraint reduces to false.
            //
            //     - Otherwise, the constraint reduces to all of the following constraint formulas:
            //
            //       - If the lambda parameters have explicitly declared types F1, ..., Fn and the function type has parameter types G1, ..., Gn, then i) for all i (1 ≤ i ≤ n), ‹Fi = Gi›, and ii) ‹T' <: T›.
            //
            //       - If the function type's return type is a (non-void) type R, assume the lambda's parameter types are the same as the function type's parameter types. Then:
            //
            //         - If R is a proper type, and if the lambda body or some result expression in the lambda body is not compatible in an assignment context with R, then false.
            //
            //         - Otherwise, if R is not a proper type, then where the lambda body has the form Expression, the constraint ‹Expression → R›; or where the lambda body is a block with result expressions e1, ..., em, for all i (1 ≤ i ≤ m), ‹ei → R›.

            // A constraint formula of the form ‹MethodReference → T›, where T mentions at least one inference variable, is reduced as follows:
            //
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
    }

    /**
     * A type S is compatible in a loose invocation context with type T
     */
    class TypeCompatibleWithType extends ConstraintFormula {
        private TIType S;
        private TIType T;

        @Override
        public Pair<BoundSet, List<ConstraintFormula>> reduce(BoundSet currentBoundSet) {
            // A constraint formula of the form ‹S → T› is reduced as follows:
            //
            // 1. If S and T are proper types, the constraint reduces to true if S is compatible in a loose invocation context with T (§5.3), and false otherwise.
            //
            // 2. Otherwise, if S is a primitive type, let S' be the result of applying boxing conversion (§5.1.7) to S. Then the constraint reduces to ‹S' → T›.
            //
            // 3. Otherwise, if T is a primitive type, let T' be the result of applying boxing conversion (§5.1.7) to T. Then the constraint reduces to ‹S = T'›.
            //
            // 4. Otherwise, if T is a parameterized type of the form G<T1, ..., Tn>, and there exists no type of the form G<...> that is a supertype of S, but the raw type G is a supertype of S, then the constraint reduces to true.
            //
            // 5. Otherwise, if T is an array type of the form G<T1, ..., Tn>[]k, and there exists no type of the form G<...>[]k that is a supertype of S, but the raw type G[]k is a supertype of S, then the constraint reduces to true. (The notation []k indicates an array type of k dimensions.)
            //
            // 6. Otherwise, the constraint reduces to ‹S <: T›.
            //
            // The fourth and fifth cases are implicit uses of unchecked conversion (§5.1.9). These, along with any use of unchecked conversion in the first case, may result in compile-time unchecked warnings, and may influence a method's invocation type (§15.12.2.6).

            throw new UnsupportedOperationException();
        }
    }

    /**
     * A reference type S is a subtype of a reference type T
     */
    class TypeSubtypeOfType extends ConstraintFormula {
        private TIType S;
        private TIType T;

        @Override
        public Pair<BoundSet, List<ConstraintFormula>> reduce(BoundSet currentBoundSet) {
            // A constraint formula of the form ‹S <: T› is reduced as follows:
            //
            // - If S and T are proper types, the constraint reduces to true if S is a subtype of T (§4.10), and false otherwise.
            //
            // - Otherwise, if S is the null type, the constraint reduces to true.
            //
            // - Otherwise, if T is the null type, the constraint reduces to false.
            //
            // - Otherwise, if S is an inference variable, α, the constraint reduces to the bound α <: T.
            //
            // - Otherwise, if T is an inference variable, α, the constraint reduces to the bound S <: α.
            //
            // - Otherwise, the constraint is reduced according to the form of T:
            //
            //   - If T is a parameterized class or interface type, or an inner class type of a parameterized class or interface type (directly or indirectly), let A1, ..., An be the type arguments of T. Among the supertypes of S, a corresponding class or interface type is identified, with type arguments B1, ..., Bn. If no such type exists, the constraint reduces to false. Otherwise, the constraint reduces to the following new constraints: for all i (1 ≤ i ≤ n), ‹Bi <= Ai›.
            //
            //   - If T is any other class or interface type, then the constraint reduces to true if T is among the supertypes of S, and false otherwise.
            //
            //   - If T is an array type, T'[], then among the supertypes of S that are array types, a most specific type is identified, S'[] (this may be S itself). If no such array type exists, the constraint reduces to false. Otherwise:
            //
            //     - If neither S' nor T' is a primitive type, the constraint reduces to ‹S' <: T'›.
            //
            //     - Otherwise, the constraint reduces to true if S' and T' are the same primitive type, and false otherwise.
            //
            //   - If T is a type variable, there are three cases:
            //
            //     - If S is an intersection type of which T is an element, the constraint reduces to true.
            //
            //     - Otherwise, if T has a lower bound, B, the constraint reduces to ‹S <: B›.
            //
            //     - Otherwise, the constraint reduces to false.
            //
            //   - If T is an intersection type, I1 & ... & In, the constraint reduces to the following new constraints: for all i (1 ≤ i ≤ n), ‹S <: Ii›.
            //

            throw new UnsupportedOperationException();
        }
    }

    /**
     * A type argument S is contained by a type argument T
     */
    class TypeContainedByType extends ConstraintFormula {
        private TIType S;
        private TIType T;

        @Override
        public Pair<BoundSet, List<ConstraintFormula>> reduce(BoundSet currentBoundSet) {
            // A constraint formula of the form ‹S <= T›, where S and T are type arguments (§4.5.1), is reduced as follows:
            //
            // - If T is a type:
            //
            //   - If S is a type, the constraint reduces to ‹S = T›.
            //
            //   - If S is a wildcard, the constraint reduces to false.
            //
            // - If T is a wildcard of the form ?, the constraint reduces to true.
            //
            // - If T is a wildcard of the form ? extends T':
            //
            //   - If S is a type, the constraint reduces to ‹S <: T'›.
            //
            //   - If S is a wildcard of the form ?, the constraint reduces to ‹Object <: T'›.
            //
            //   - If S is a wildcard of the form ? extends S', the constraint reduces to ‹S' <: T'›.
            //
            //   - If S is a wildcard of the form ? super S', the constraint reduces to ‹Object = T'›.
            //
            // - If T is a wildcard of the form ? super T':
            //
            //   - If S is a type, the constraint reduces to ‹T' <: S›.
            //
            //   - If S is a wildcard of the form ? super S', the constraint reduces to ‹T' <: S'›.
            //
            //   - Otherwise, the constraint reduces to false.

            throw new UnsupportedOperationException();
        }
    }

    /**
     * A type S is the same as a type T (§4.3.4), or a type argument S is the same as type argument T
     */
    class TypeSameAsType extends ConstraintFormula {
        private TIType S;
        private TIType T;

        @Override
        public Pair<BoundSet, List<ConstraintFormula>> reduce(BoundSet currentBoundSet) {
            // A constraint formula of the form ‹S = T›, where S and T are types, is reduced as follows:
            //
            // - If S and T are proper types, the constraint reduces to true if S is the same as T (§4.3.4), and false otherwise.
            //
            // - Otherwise, if S or T is the null type, the constraint reduces to false.
            //
            // - Otherwise, if S is an inference variable, α, and T is not a primitive type, the constraint reduces to the bound α = T.
            //
            // - Otherwise, if T is an inference variable, α, and S is not a primitive type, the constraint reduces to the bound S = α.
            //
            // - Otherwise, if S and T are class or interface types with the same erasure, where S has type arguments B1, ..., Bn and T has type arguments A1, ..., An, the constraint reduces to the following new constraints: for all i (1 ≤ i ≤ n), ‹Bi = Ai›.
            //
            // - Otherwise, if S and T are array types, S'[] and T'[], the constraint reduces to ‹S' = T'›.
            //
            // - Otherwise, the constraint reduces to false.
            //
            // Note that we do not address intersection types above, because it is impossible for reduction to encounter an intersection type that is not a proper type.

            // A constraint formula of the form ‹S = T›, where S and T are type arguments (§4.5.1), is reduced as follows:
            //
            // - If S and T are types, the constraint is reduced as described above.
            //
            // - If S has the form ? and T has the form ?, the constraint reduces to true.
            //
            // - If S has the form ? and T has the form ? extends T', the constraint reduces to ‹Object = T'›.
            //
            // - If S has the form ? extends S' and T has the form ?, the constraint reduces to ‹S' = Object›.
            //
            // - If S has the form ? extends S' and T has the form ? extends T', the constraint reduces to ‹S' = T'›.
            //
            // - If S has the form ? super S' and T has the form ? super T', the constraint reduces to ‹S' = T'›.
            //
            // - Otherwise, the constraint reduces to false.


            throw new UnsupportedOperationException();
        }
    }

    /**
     * The checked exceptions thrown by the body of the LambdaExpression are declared by the throws clause of the
     * function type derived from T.
     */
    class LambdaThrowsCompatibleWithType extends ConstraintFormula {
        private LambdaExpr lambdaExpression;
        private TIType T;

        @Override
        public Pair<BoundSet, List<ConstraintFormula>> reduce(BoundSet currentBoundSet) {
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

    /**
     * The checked exceptions thrown by the referenced method are declared by the throws clause of the function type
     * derived from T.
     */
    class MethodReferenceThrowsCompatibleWithType extends ConstraintFormula {
        private MethodReferenceExpr methodReference;
        private TIType T;

        @Override
        public Pair<BoundSet, List<ConstraintFormula>> reduce(BoundSet currentBoundSet) {
            // A constraint formula of the form ‹MethodReference →throws T› is reduced as follows:
            //
            // - If T is not a functional interface type, or if T is a functional interface type but does not have a function type (§9.9), the constraint reduces to false.
            //
            // - Otherwise, let the target function type for the method reference expression be the function type of T. If the method reference is inexact (§15.13.1) and one or more of the function type's parameter types is not a proper type, the constraint reduces to false.
            //
            // - Otherwise, if the method reference is inexact and the function type's result is neither void nor a proper type, the constraint reduces to false.
            //
            // - Otherwise, let E1, ..., En be the types in the function type's throws clause that are not proper types. Let X1, ..., Xm be the checked exceptions in the throws clause of the invocation type of the method reference's compile-time declaration (§15.13.2) (as derived from the function type's parameter types and return type). Then there are two cases:
            //
            //   - If n = 0 (the function type's throws clause consists only of proper types), then if there exists some i (1 ≤ i ≤ m) such that Xi is not a subtype of any proper type in the throws clause, the constraint reduces to false; otherwise, the constraint reduces to true.
            //
            //   - If n > 0, the constraint reduces to a set of subtyping constraints: for all i (1 ≤ i ≤ m), if Xi is not a subtype of any proper type in the throws clause, then the constraints include, for all j (1 ≤ j ≤ n), ‹Xi <: Ej›. In addition, for all j (1 ≤ j ≤ n), the constraint reduces to the bound throws Ej.

            throw new UnsupportedOperationException();
        }
    }

}

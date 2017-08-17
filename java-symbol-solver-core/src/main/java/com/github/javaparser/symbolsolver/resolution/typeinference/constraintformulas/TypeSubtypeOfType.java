package com.github.javaparser.symbolsolver.resolution.typeinference.constraintformulas;

import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typeinference.BoundSet;
import com.github.javaparser.symbolsolver.resolution.typeinference.ConstraintFormula;

/**
 * A reference type S is a subtype of a reference type T
 */
public class TypeSubtypeOfType extends ConstraintFormula {
    private Type S;
    private Type T;

    public TypeSubtypeOfType(Type S, Type T) {
        this.S = S;
        this.T = T;
    }

    @Override
    public ReductionResult reduce(BoundSet currentBoundSet) {
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
package com.github.javaparser.symbolsolver.resolution.typeinference.constraintformulas;

import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typeinference.BoundSet;
import com.github.javaparser.symbolsolver.resolution.typeinference.ConstraintFormula;

/**
 * A type S is the same as a type T (§4.3.4), or a type argument S is the same as type argument T
 */
public class TypeSameAsType extends ConstraintFormula {
    private Type S;
    private Type T;

    public TypeSameAsType(Type s, Type t) {
        S = s;
        T = t;
    }

    @Override
    public ReductionResult reduce(BoundSet currentBoundSet) {
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

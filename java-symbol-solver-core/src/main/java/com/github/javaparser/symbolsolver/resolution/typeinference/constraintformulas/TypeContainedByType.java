package com.github.javaparser.symbolsolver.resolution.typeinference.constraintformulas;

import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typeinference.BoundSet;
import com.github.javaparser.symbolsolver.resolution.typeinference.ConstraintFormula;

/**
 * A type argument S is contained by a type argument T
 */
public class TypeContainedByType extends ConstraintFormula {
    private Type S;
    private Type T;

    @Override
    public ReductionResult reduce(BoundSet currentBoundSet) {
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

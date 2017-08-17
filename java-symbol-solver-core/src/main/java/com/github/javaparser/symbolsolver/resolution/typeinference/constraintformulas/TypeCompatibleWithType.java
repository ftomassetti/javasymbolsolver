package com.github.javaparser.symbolsolver.resolution.typeinference.constraintformulas;

import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typeinference.BoundSet;
import com.github.javaparser.symbolsolver.resolution.typeinference.ConstraintFormula;
import com.github.javaparser.symbolsolver.resolution.typeinference.TIType;
import com.github.javaparser.utils.Pair;

import java.util.List;

/**
 * A type S is compatible in a loose invocation context with type T
 */
public class TypeCompatibleWithType extends ConstraintFormula {
    private Type S;
    private Type T;

    @Override
    public ReductionResult reduce(BoundSet currentBoundSet) {
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

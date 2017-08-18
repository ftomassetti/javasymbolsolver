package com.github.javaparser.symbolsolver.resolution.typeinference.constraintformulas;

import com.github.javaparser.symbolsolver.model.typesystem.ReferenceTypeImpl;
import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typeinference.BoundSet;
import com.github.javaparser.symbolsolver.resolution.typeinference.ConstraintFormula;
import com.github.javaparser.symbolsolver.resolution.typeinference.TypeHelper;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import static com.github.javaparser.symbolsolver.resolution.typeinference.TypeHelper.isCompatibleInALooseInvocationContext;
import static com.github.javaparser.symbolsolver.resolution.typeinference.TypeHelper.isProperType;

/**
 * A type S is compatible in a loose invocation context with type T
 */
public class TypeCompatibleWithType extends ConstraintFormula {
    private Type s;
    private Type t;

    public TypeCompatibleWithType(Type s, Type t) {
        this.s = s;
        this.t = t;
    }

    @Override
    public ReductionResult reduce(BoundSet currentBoundSet) {
        // A constraint formula of the form ‹S → T› is reduced as follows:
        //
        // 1. If S and T are proper types, the constraint reduces to true if S is compatible in a loose invocation context with T (§5.3), and false otherwise.

        if (isProperType(s) && isProperType(t)) {
            if (isCompatibleInALooseInvocationContext(s, t)) {
                return ReductionResult.trueResult();
            } else {
                return ReductionResult.falseResult();
            }
        }

        // 2. Otherwise, if S is a primitive type, let S' be the result of applying boxing conversion (§5.1.7) to S. Then the constraint reduces to ‹S' → T›.

        if (s.isPrimitive()) {
            ReflectionTypeSolver typeSolver = new ReflectionTypeSolver();
            Type sFirst = new ReferenceTypeImpl(typeSolver.solveType(s.asPrimitive().getBoxTypeQName()), typeSolver);
            return ReductionResult.oneConstraint(new TypeCompatibleWithType(sFirst, t));
        }

        // 3. Otherwise, if T is a primitive type, let T' be the result of applying boxing conversion (§5.1.7) to T. Then the constraint reduces to ‹S = T'›.

        if (t.isPrimitive()) {
            ReflectionTypeSolver typeSolver = new ReflectionTypeSolver();
            Type tFirst = new ReferenceTypeImpl(typeSolver.solveType(t.asPrimitive().getBoxTypeQName()), typeSolver);
            return ReductionResult.oneConstraint(new TypeSameAsType(s, tFirst));
        }

        // The fourth and fifth cases are implicit uses of unchecked conversion (§5.1.9). These, along with any use of
        // unchecked conversion in the first case, may result in compile-time unchecked warnings, and may influence a
        // method's invocation type (§15.12.2.6).

        // 4. Otherwise, if T is a parameterized type of the form G<T1, ..., Tn>, and there exists no type of the form G<...> that is a supertype of S, but the raw type G is a supertype of S, then the constraint reduces to true.

        if (t.isReferenceType() && !t.asReferenceType().getTypeDeclaration().getTypeParameters().isEmpty()) {
            throw new UnsupportedOperationException();
        }

        // 5. Otherwise, if T is an array type of the form G<T1, ..., Tn>[]k, and there exists no type of the form G<...>[]k that is a supertype of S, but the raw type G[]k is a supertype of S, then the constraint reduces to true. (The notation []k indicates an array type of k dimensions.)

        if (t.isArray()) {
            throw new UnsupportedOperationException();
        }

        // 6. Otherwise, the constraint reduces to ‹S <: T›

        return ReductionResult.empty().withConstraint(new TypeSubtypeOfType(s, t));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeCompatibleWithType that = (TypeCompatibleWithType) o;

        if (!s.equals(that.s)) return false;
        return t.equals(that.t);
    }

    @Override
    public int hashCode() {
        int result = s.hashCode();
        result = 31 * result + t.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TypeCompatibleWithType{" +
                "s=" + s +
                ", t=" + t +
                '}';
    }
}
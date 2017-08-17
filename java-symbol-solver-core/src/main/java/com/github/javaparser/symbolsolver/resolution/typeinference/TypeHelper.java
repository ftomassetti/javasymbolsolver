package com.github.javaparser.symbolsolver.resolution.typeinference;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.symbolsolver.model.typesystem.ReferenceType;
import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.model.typesystem.Wildcard;

/**
 * The term "type" is used loosely in this chapter to include type-like syntax that contains inference variables.
 *
 * Assertions that involve inference
 * variables are assertions about every proper type that can be produced by replacing each inference variable with
 * a proper type.
 */
public class TypeHelper {

    /**
     * The term proper type excludes such "types" that mention inference variables.
     */
    public static boolean isProperType(Type type) {
        if (type instanceof InferenceVariable) {
            return false;
        }
        if (type instanceof ReferenceType) {
            ReferenceType referenceType = (ReferenceType) type;
            return referenceType.typeParametersValues().stream().allMatch(it -> isProperType(it));
        }
        if (type instanceof Wildcard) {
            Wildcard wildcard = (Wildcard)type;
            if (wildcard.isBounded()) {
                return isProperType(wildcard.getBoundedType());
            } else {
                return true;
            }
        }
        throw new UnsupportedOperationException(type.toString());
    }

    public static boolean isCompatibleInALooseInvocationContext(Expression expression, Type t) {
        throw new UnsupportedOperationException();
    }

    public static boolean isInferenceVariable(Type type) {
        return type instanceof InferenceVariable;
    }
}

package com.github.javaparser.symbolsolver.resolution.typeinference;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.symbolsolver.model.typesystem.Type;

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
        return !(type instanceof InferenceVariable);
    }

    public static boolean isCompatibleInALooseInvocationContext(Expression expression, Type t) {
        throw new UnsupportedOperationException();
    }

    public static boolean isInferenceVariable(Type type) {
        return type instanceof InferenceVariable;
    }
}

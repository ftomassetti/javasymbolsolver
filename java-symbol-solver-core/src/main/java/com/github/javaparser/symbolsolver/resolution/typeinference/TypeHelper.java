package com.github.javaparser.symbolsolver.resolution.typeinference;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.model.typesystem.PrimitiveType;
import com.github.javaparser.symbolsolver.model.typesystem.ReferenceType;
import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.model.typesystem.Wildcard;

import java.util.Optional;

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

    /**
     * see https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.3
     * @param expression
     * @param t
     * @return
     */
    public static boolean isCompatibleInAStrictInvocationContext(Expression expression, Type t) {
        throw new UnsupportedOperationException();
    }

    /**
     * see https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.3
     * @param expression
     * @param t
     * @return
     */
    public static boolean isCompatibleInALooseInvocationContext(TypeSolver typeSolver, Expression expression, Type t) {
        //throw new UnsupportedOperationException("Unable to determine if " + expression + " is compatible in a loose invocation context with type " + t);
        return isCompatibleInALooseInvocationContext(JavaParserFacade.get(typeSolver).getType(expression), t);
    }

    /**
     * see https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.3
     * @param s
     * @param t
     * @return
     */
    public static boolean isCompatibleInALooseInvocationContext(Type s, Type t) {
        // Loose invocation contexts allow a more permissive set of conversions, because they are only used for a
        // particular invocation if no applicable declaration can be found using strict invocation contexts. Loose
        // invocation contexts allow the use of one of the following:
        //
        // - an identity conversion (§5.1.1)

        if (s.equals(t)) {
            return true;
        }

        // - a widening primitive conversion (§5.1.2)

        if (s.isPrimitive() && t.isPrimitive() && areCompatibleThroughWideningPrimitiveConversion(s, t)) {
            return true;
        }

        // - a widening reference conversion (§5.1.5)

        if (s.isReferenceType() && t.isReferenceType() && areCompatibleThroughWideningReferenceConversion(s, t)) {
            return true;
        }

        // - a boxing conversion (§5.1.7) optionally followed by widening reference conversion

        if (s.isPrimitive() && t.isReferenceType() &&
                areCompatibleThroughWideningReferenceConversion(toBoxedType(s.asPrimitive()), t)) {
            return true;
        }

        // - an unboxing conversion (§5.1.8) optionally followed by a widening primitive conversion

        if (s.isReferenceType() && t.isPrimitive() &&
                areCompatibleThroughWideningPrimitiveConversion(toUnboxedType(s.asReferenceType()), t)) {
            return true;
        }

        // If, after the conversions listed for an invocation context have been applied, the resulting type is a raw
        // type (§4.8), an unchecked conversion (§5.1.9) may then be applied.
        //
        // A value of the null type (the null reference is the only such value) may be assigned to any reference type
        if (s.isNull() && t.isReferenceType()) {
            return true;
        }

        throw new UnsupportedOperationException("isCompatibleInALooseInvocationContext unable to decide on s=" + s + ", t=" + t);
    }

    private static Type toUnboxedType(ReferenceType referenceType) {
        throw new UnsupportedOperationException();
    }

    private static Type toBoxedType(PrimitiveType primitiveType) {
        throw new UnsupportedOperationException();
    }

    private static boolean areCompatibleThroughWideningReferenceConversion(Type s, Type t) {
        Optional<PrimitiveType> correspondingPrimitiveTypeForS = PrimitiveType.ALL.stream().filter(pt -> pt.getBoxTypeQName().equals(s.asReferenceType().getQualifiedName())).findFirst();
        if (!correspondingPrimitiveTypeForS.isPresent()) {
            return false;
        }
        throw new UnsupportedOperationException("areCompatibleThroughWideningReferenceConversion s="+s+", t=" + t);
    }

    private static boolean areCompatibleThroughWideningPrimitiveConversion(Type s, Type t) {
        throw new UnsupportedOperationException();
    }

    public static boolean isInferenceVariable(Type type) {
        return type instanceof InferenceVariable;
    }
}

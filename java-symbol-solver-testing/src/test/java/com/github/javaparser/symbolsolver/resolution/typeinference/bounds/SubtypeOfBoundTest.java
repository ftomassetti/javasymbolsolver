package com.github.javaparser.symbolsolver.resolution.typeinference.bounds;

import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.model.typesystem.ReferenceTypeImpl;
import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typeinference.*;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class SubtypeOfBoundTest {

    private TypeSolver typeSolver = new ReflectionTypeSolver();
    private Type stringType = new ReferenceTypeImpl(new ReflectionTypeSolver().solveType(String.class.getCanonicalName()), typeSolver);
    private Type integerType = new ReferenceTypeImpl(new ReflectionTypeSolver().solveType(Integer.class.getCanonicalName()), typeSolver);
    private Type doubleType = new ReferenceTypeImpl(new ReflectionTypeSolver().solveType(Double.class.getCanonicalName()), typeSolver);
    private Type objectType = new ReferenceTypeImpl(new ReflectionTypeSolver().solveType(Object.class.getCanonicalName()), typeSolver);

    @Test
    public void recognizeProperLowerBound1() {
        // { Integer <: α, Double <: α, α <: Object } describes two proper lower bounds and one proper upper bound for α.

        InferenceVariable inferenceVariable = new InferenceVariable("α");
        Bound bound = new SubtypeOfBound(integerType, inferenceVariable);

        assertEquals(Optional.of(new ProperLowerBound(inferenceVariable, integerType)), bound.isProperLowerBound());
    }

    @Test
    public void recognizeProperLowerBound2() {
        // { Integer <: α, Double <: α, α <: Object } describes two proper lower bounds and one proper upper bound for α.

        InferenceVariable inferenceVariable = new InferenceVariable("α");
        Bound bound = new SubtypeOfBound(doubleType, inferenceVariable);

        assertEquals(Optional.of(new ProperLowerBound(inferenceVariable, doubleType)), bound.isProperLowerBound());
    }

    @Test
    public void recognizeProperUpperBound1() {
        // { Integer <: α, Double <: α, α <: Object } describes two proper lower bounds and one proper upper bound for α.

        InferenceVariable inferenceVariable = new InferenceVariable("α");
        Bound bound = new SubtypeOfBound(inferenceVariable, objectType);

        assertEquals(Optional.of(new ProperUpperBound(inferenceVariable, objectType)), bound.isProperUpperBound());
    }

    @Test
    public void recognizeProperUpperBound2() {
        // { α <: Iterable<?>, β <: Object, α <: List<β> } describes a proper upper bound for each of α and β, along with a dependency between them.
        throw new UnsupportedOperationException();
    }
}

package com.github.javaparser.symbolsolver.resolution.typeinference.constraintformulas;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.model.typesystem.ReferenceTypeImpl;
import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typeinference.BoundSet;
import com.github.javaparser.symbolsolver.resolution.typeinference.ConstraintFormula;
import com.github.javaparser.symbolsolver.resolution.typeinference.InferenceVariable;
import com.github.javaparser.symbolsolver.resolution.typeinference.constraintformulas.ExpressionCompatibleWithType;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ConstraintFormulaTest {

    private TypeSolver typeSolver = new ReflectionTypeSolver();
    private Type stringType = new ReferenceTypeImpl(new ReflectionTypeSolver().solveType(String.class.getCanonicalName()), typeSolver);

    /**
     * From JLS 18.1.2
     *
     * From Collections.singleton("hi"), we have the constraint formula ‹"hi" → α›.
     * Through reduction, this will become the constraint formula: ‹String <: α›.
     */
    @Test
    public void testExpressionCompatibleWithTypeReduce1() {
        Expression e = new StringLiteralExpr("hi");
        InferenceVariable inferenceVariable = new InferenceVariable("α");

        ExpressionCompatibleWithType formula = new ExpressionCompatibleWithType(e, inferenceVariable);

        assertEquals(
                ConstraintFormula.ReductionResult.empty().withConstraint(new TypeSubtypeOfType(stringType, inferenceVariable)),
                formula.reduce(BoundSet.empty()));
    }

    /**
     * From JLS 18.1.2
     *
     * From Arrays.asList(1, 2.0), we have the constraint formulas ‹1 → α› and ‹2.0 → α›. Through reduction,
     * these will become the constraint formulas ‹int → α› and ‹double → α›, and then ‹Integer <: α› and ‹Double <: α›.
     */
    @Test
    public void testExpressionCompatibleWithTypeReduce2() {
        throw new UnsupportedOperationException();
    }

    /**
     * From JLS 18.1.2
     *
     * From the target type of the constructor invocation List<Thread> lt = new ArrayList<>(), we have the constraint
     * formula ‹ArrayList<α> → List<Thread>›. Through reduction, this will become the constraint formula ‹α <= Thread›,
     * and then ‹α = Thread›.
     */
    @Test
    public void testExpressionCompatibleWithTypeReduce3() {
        throw new UnsupportedOperationException();
    }
}

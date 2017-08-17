package com.github.javaparser.symbolsolver.resolution.typeinference;

import java.util.LinkedList;
import java.util.List;

public class ConstraintFormulaSet {
    private List<ConstraintFormula> constraintFormulas;

    public ConstraintFormulaSet addConstraint(ConstraintFormula constraintFormula) {
        ConstraintFormulaSet newInstance = new ConstraintFormulaSet();
        newInstance.constraintFormulas.addAll(this.constraintFormulas);
        newInstance.constraintFormulas.add(constraintFormula);
        return newInstance;
    }

    private static final ConstraintFormulaSet EMPTY = new ConstraintFormulaSet();

    public static ConstraintFormulaSet empty() {
        return EMPTY;
    }

    private ConstraintFormulaSet() {
        constraintFormulas = new LinkedList<>();
    }

    public BoundSet reduce() {
        throw new UnsupportedOperationException();
    }
}

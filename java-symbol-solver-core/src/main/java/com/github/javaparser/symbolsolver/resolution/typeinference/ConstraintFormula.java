package com.github.javaparser.symbolsolver.resolution.typeinference;

import java.util.LinkedList;
import java.util.List;

/**
 * Constraint formulas are assertions of compatibility or subtyping that may involve inference variables.
 */
public abstract class ConstraintFormula {

    public static class ReductionResult {
        private BoundSet boundSet;
        private List<ConstraintFormula> constraintFormulas;

        public static ReductionResult empty() {
            return new ReductionResult();
        }

        public ReductionResult withConstraint(ConstraintFormula constraintFormula) {
            ReductionResult newInstance = new ReductionResult();
            newInstance.boundSet = this.boundSet;
            newInstance.constraintFormulas = new LinkedList<>();
            newInstance.constraintFormulas.addAll(this.constraintFormulas);
            newInstance.constraintFormulas.add(constraintFormula);
            return newInstance;
        }

        public ReductionResult withBound(Bound bound) {
            ReductionResult newInstance = new ReductionResult();
            newInstance.boundSet = this.boundSet.withBound(bound);
            newInstance.constraintFormulas = this.constraintFormulas;
            return newInstance;
        }

        private ReductionResult() {
            this.boundSet = BoundSet.empty();
            this.constraintFormulas = new LinkedList<>();
        }

        public static ReductionResult trueResult() {
            return empty();
        }

        public static ReductionResult falseResult() {
            return empty().withBound(Bound.falseBound());
        }
    }

    /**
     * A formula is reduced to one or both of:
     * i) A bound or bound set, which is to be incorporated with the "current" bound set. Initially, the current bound
     *    set is empty.
     * ii) Further constraint formulas, which are to be reduced recursively.
     */
    public abstract ReductionResult reduce(BoundSet currentBoundSet);

}

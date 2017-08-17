package com.github.javaparser.symbolsolver.resolution.typeinference;

import java.util.List;

/**
 * See https://docs.oracle.com/javase/specs/jls/se8/html/jls-18.html
 */
public class TypeInference extends TIType {

    /**
     * Takes a compatibility assertion about an expression or type, called a constraint formula, and reduces it to a
     * set of bounds on inference variables. Often, a constraint formula reduces to other constraint formulas,
     * which must be recursively reduced. A procedure is followed to identify these additional constraint formulas and,
     * ultimately, to express via a bound set the conditions under which the choices for inferred types would render
     * each constraint formula true.
     */
    public BoundSet performReduction(List<ConstraintFormula> constraints) {
        throw new UnsupportedOperationException();
    }

    /**
     * Maintains a set of inference variable bounds, ensuring that these are consistent as new bounds are added.
     * Because the bounds on one variable can sometimes impact the possible choices for another variable, this process
     * propagates bounds between such interdependent variables.
     */
    public BoundSet performIncorporation(BoundSet initialBoundSet) {
        throw new UnsupportedOperationException();
    }

    /**
     * Examines the bounds on an inference variable and determines an instantiation that is compatible with those
     * bounds. It also decides the order in which interdependent inference variables are to be resolved.
     */
    public Instantiation performResolution(BoundSet boundSet) {
        throw new UnsupportedOperationException();
    }

}

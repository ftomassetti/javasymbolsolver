package com.github.javaparser.symbolsolver.resolution.typeinference.bounds;

import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typeinference.Bound;
import com.github.javaparser.symbolsolver.resolution.typeinference.InferenceVariable;

import java.util.List;

/**
 * Capture(G<A1, ..., An>): The variables α1, ..., αn represent the result of capture conversion (§5.1.10)
 * applied to G<A1, ..., An> (where A1, ..., An may be types or wildcards and may mention inference variables).
 */
public class CapturesBound extends Bound {
    private List<InferenceVariable> inferenceVariables;
    private List<Type> typesOrWildcards;
}

package com.github.javaparser.symbolsolver.resolution.typeinference;

import java.util.LinkedList;
import java.util.List;

public class InstantiationSet {

    private List<Instantiation> instantiations;

    public boolean allInferenceVariablesAreResolved(BoundSet boundSet) {
        throw new UnsupportedOperationException();
    }

    public static InstantiationSet empty() {
        return EMPTY;
    }

    private static final InstantiationSet EMPTY = new InstantiationSet();

    private InstantiationSet() {
        instantiations = new LinkedList<>();
    }

    public InstantiationSet withInstantiation(Instantiation instantiation) {
        InstantiationSet newInstance = new InstantiationSet();
        newInstance.instantiations.addAll(this.instantiations);
        newInstance.instantiations.add(instantiation);
        return newInstance;
    }

    public boolean isEmpty() {
        return instantiations.isEmpty();
    }
}

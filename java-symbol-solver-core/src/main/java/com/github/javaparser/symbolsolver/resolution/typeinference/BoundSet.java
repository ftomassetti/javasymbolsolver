package com.github.javaparser.symbolsolver.resolution.typeinference;

import java.util.LinkedList;
import java.util.List;

public class BoundSet {

    private List<Bound> bounds = new LinkedList<>();

    /**
     * It is sometimes convenient to refer to an empty bound set with the symbol true; this is merely out of
     * convenience, and the two are interchangeable.
     */
    public boolean isTrue() {
        return bounds.isEmpty();
    }
}

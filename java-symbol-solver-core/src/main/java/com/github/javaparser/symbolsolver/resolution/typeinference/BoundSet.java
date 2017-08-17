package com.github.javaparser.symbolsolver.resolution.typeinference;

import com.github.javaparser.symbolsolver.resolution.typeinference.bounds.FalseBound;

import java.util.LinkedList;
import java.util.List;

public class BoundSet {

    private List<Bound> bounds = new LinkedList<>();

    private static final BoundSet EMPTY = new BoundSet();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoundSet boundSet = (BoundSet) o;

        return bounds.equals(boundSet.bounds);
    }

    @Override
    public int hashCode() {
        return bounds.hashCode();
    }

    @Override
    public String toString() {
        return "BoundSet{" +
                "bounds=" + bounds +
                '}';
    }

    /**

     * It is sometimes convenient to refer to an empty bound set with the symbol true; this is merely out of
     * convenience, and the two are interchangeable.
     */
    public boolean isTrue() {
        return bounds.isEmpty();
    }

    public static BoundSet empty() {
        return EMPTY;
    }

    public BoundSet withBound(Bound bound) {
        BoundSet boundSet = new BoundSet();
        boundSet.bounds.addAll(this.bounds);
        boundSet.bounds.add(bound);
        return boundSet;
    }

    public BoundSet incorporate(BoundSet otherBounds) {
        throw new UnsupportedOperationException();
    }

    public boolean containsFalse() {
        return bounds.stream().anyMatch(it -> it instanceof FalseBound);
    }
}

package com.github.javaparser.symbolsolver.model.typesystem;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An intersection type is defined in java as list of types separates by ampersands.
 *
 * @author Federico Tomassetti
 */
public class IntersectionType implements Type {
    private List<Type> elements;

    public IntersectionType(Collection<Type> elements) {
        if (elements.size() < 2) {
            throw new IllegalArgumentException("An intersection type should have at least two elements. This has " + elements.size());
        }
        this.elements = new LinkedList<>(elements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntersectionType that = (IntersectionType) o;

        return new HashSet<>(elements).equals(new HashSet<>(that.elements));
    }

    @Override
    public int hashCode() {
        return new HashSet<>(elements).hashCode();
    }

    @Override
    public String describe() {
        return String.join(" & ", elements.stream().map(Type::describe).collect(Collectors.toList()));
    }

    @Override
    public boolean isAssignableBy(Type other) {
        return elements.stream().allMatch(e -> e.isAssignableBy(other));
    }
}

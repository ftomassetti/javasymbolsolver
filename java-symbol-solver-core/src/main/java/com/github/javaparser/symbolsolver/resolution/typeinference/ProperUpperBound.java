package com.github.javaparser.symbolsolver.resolution.typeinference;

import com.github.javaparser.symbolsolver.model.typesystem.Type;

/**
 * Created by federico on 17/08/2017.
 */
public class ProperUpperBound {
    private InferenceVariable inferenceVariable;
    private Type properType;

    public ProperUpperBound(InferenceVariable inferenceVariable, Type properType) {
        this.inferenceVariable = inferenceVariable;
        this.properType = properType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProperUpperBound that = (ProperUpperBound) o;

        if (!inferenceVariable.equals(that.inferenceVariable)) return false;
        return properType.equals(that.properType);
    }

    @Override
    public int hashCode() {
        int result = inferenceVariable.hashCode();
        result = 31 * result + properType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ProperUpperBound{" +
                "inferenceVariable=" + inferenceVariable +
                ", properType=" + properType +
                '}';
    }
}

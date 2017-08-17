package com.github.javaparser.symbolsolver.resolution.typeinference;

import com.github.javaparser.symbolsolver.model.typesystem.Type;

/**
 * The term "type" is used loosely in this chapter to include type-like syntax that contains inference variables.
 * The term proper type excludes such "types" that mention inference variables. Assertions that involve inference
 * variables are assertions about every proper type that can be produced by replacing each inference variable with
 * a proper type.
 */
@Deprecated
public abstract class TIType {

    class ProperType extends TIType {
        private Type type;
    }

}

package com.github.javaparser.symbolsolver.resolution.typeinference;

import java.util.List;

/**
 * A variety of compile-time analyses require reasoning about types that are not yet known. Principal among these are
 * generic method applicability testing (§18.5.1) and generic method invocation type inference (§18.5.2). In general,
 * we refer to the process of reasoning about unknown types as type inference.
 *
 * At a high level, type inference can be decomposed into three processes: Reduction, Incorporation, and Resolution.
 *
 * See https://docs.oracle.com/javase/specs/jls/se8/html/jls-18.html
 */
public class TypeInference {

    /**
     * Maintains a set of inference variable bounds, ensuring that these are consistent as new bounds are added.
     * Because the bounds on one variable can sometimes impact the possible choices for another variable, this process
     * propagates bounds between such interdependent variables.
     */
    public BoundSet performIncorporation(BoundSet initialBoundSet) {

        // As bound sets are constructed and grown during inference, it is possible that new bounds can be inferred based on the assertions of the original bounds. The process of incorporation identifies these new bounds and adds them to the bound set.
        //
        // Incorporation can happen in two scenarios. One scenario is that the bound set contains complementary pairs of bounds; this implies new constraint formulas, as specified in §18.3.1. The other scenario is that the bound set contains a bound involving capture conversion; this implies new bounds and may imply new constraint formulas, as specified in §18.3.2. In both scenarios, any new constraint formulas are reduced, and any new bounds are added to the bound set. This may trigger further incorporation; ultimately, the set will reach a fixed point and no further bounds can be inferred.
        //
        // If incorporation of a bound set has reached a fixed point, and the set does not contain the bound false, then the bound set has the following properties:
        //
        // -For each combination of a proper lower bound L and a proper upper bound U of an inference variable, L <: U.
        //
        // - If every inference variable mentioned by a bound has an instantiation, the bound is satisfied by the corresponding substitution.
        //
        // - Given a dependency α = β, every bound of α matches a bound of β, and vice versa.
        //
        // - Given a dependency α <: β, every lower bound of α is a lower bound of β, and every upper bound of β is an upper bound of α.

        throw new UnsupportedOperationException();
    }

    /**
     * Examines the bounds on an inference variable and determines an instantiation that is compatible with those
     * bounds. It also decides the order in which interdependent inference variables are to be resolved.
     */
    public InstantiationSet performResolution(BoundSet boundSet) {
        // Given a bound set that does not contain the bound false, a subset of the inference variables mentioned by the bound set may be resolved. This means that a satisfactory instantiation may be added to the set for each inference variable, until all the requested variables have instantiations.
        //
        // Dependencies in the bound set may require that the variables be resolved in a particular order, or that additional variables be resolved. Dependencies are specified as follows:
        //
        // - Given a bound of one of the following forms, where T is either an inference variable β or a type that mentions β:
        //
        //   - α = T
        //
        //   - α <: T
        //
        //   - T = α
        //
        //   - T <: α
        //
        //   If α appears on the left-hand side of another bound of the form G<..., α, ...> = capture(G<...>), then β depends on the resolution of α. Otherwise, α depends on the resolution of β.
        //
        // - An inference variable α appearing on the left-hand side of a bound of the form G<..., α, ...> = capture(G<...>) depends on the resolution of every other inference variable mentioned in this bound (on both sides of the = sign).
        //
        // - An inference variable α depends on the resolution of an inference variable β if there exists an inference variable γ such that α depends on the resolution of γ and γ depends on the resolution of β.
        //
        // - An inference variable α depends on the resolution of itself.
        //
        // Given a set of inference variables to resolve, let V be the union of this set and all variables upon which the resolution of at least one variable in this set depends.
        //
        // If every variable in V has an instantiation, then resolution succeeds and this procedure terminates.
        //
        // Otherwise, let { α1, ..., αn } be a non-empty subset of uninstantiated variables in V such that i) for all i (1 ≤ i ≤ n), if αi depends on the resolution of a variable β, then either β has an instantiation or there is some j such that β = αj; and ii) there exists no non-empty proper subset of { α1, ..., αn } with this property. Resolution proceeds by generating an instantiation for each of α1, ..., αn based on the bounds in the bound set:
        //
        // - If the bound set does not contain a bound of the form G<..., αi, ...> = capture(G<...>) for all i (1 ≤ i ≤ n), then a candidate instantiation Ti is defined for each αi:
        //
        //   - If αi has one or more proper lower bounds, L1, ..., Lk, then Ti = lub(L1, ..., Lk) (§4.10.4).
        //
        //   - Otherwise, if the bound set contains throws αi, and the proper upper bounds of αi are, at most, Exception, Throwable, and Object, then Ti = RuntimeException.
        //
        //   - Otherwise, where αi has proper upper bounds U1, ..., Uk, Ti = glb(U1, ..., Uk) (§5.1.10).
        //
        //   The bounds α1 = T1, ..., αn = Tn are incorporated with the current bound set.
        //
        //   If the result does not contain the bound false, then the result becomes the new bound set, and resolution proceeds by selecting a new set of variables to instantiate (if necessary), as described above.
        //
        //   Otherwise, the result contains the bound false, so a second attempt is made to instantiate { α1, ..., αn } by performing the step below.
        //
        // - If the bound set contains a bound of the form G<..., αi, ...> = capture(G<...>) for some i (1 ≤ i ≤ n), or;
        //
        //   If the bound set produced in the step above contains the bound false;
        //
        //   then let Y1, ..., Yn be fresh type variables whose bounds are as follows:
        //
        //   - For all i (1 ≤ i ≤ n), if αi has one or more proper lower bounds L1, ..., Lk, then let the lower bound of Yi be lub(L1, ..., Lk); if not, then Yi has no lower bound.
        //
        //   - For all i (1 ≤ i ≤ n), where αi has upper bounds U1, ..., Uk, let the upper bound of Yi be glb(U1 θ, ..., Uk θ), where θ is the substitution [α1:=Y1, ..., αn:=Yn].
        //
        //   If the type variables Y1, ..., Yn do not have well-formed bounds (that is, a lower bound is not a subtype of an upper bound, or an intersection type is inconsistent), then resolution fails.
        //
        //   Otherwise, for all i (1 ≤ i ≤ n), all bounds of the form G<..., αi, ...> = capture(G<...>) are removed from the current bound set, and the bounds α1 = Y1, ..., αn = Yn are incorporated.
        //
        //   If the result does not contain the bound false, then the result becomes the new bound set, and resolution proceeds by selecting a new set of variables to instantiate (if necessary), as described above.
        //
        //   Otherwise, the result contains the bound false, and resolution fails.

        throw new UnsupportedOperationException();
    }

}

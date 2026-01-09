package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.exceptions.DomainValidationException;

/**
 * <h2>Feature Reconstitution Factory (Pure Domain Pattern)</h2>
 *
 * <p><b>Role:</b> Acts as the "Activation Point" for Feature entities. It is
 * decoupled from the Product Aggregate to maintain strict DDD boundaries.</p>
 *
 * <h3>Developer Implementation Notes:</h3>
 * <ul>
 *   <li><b>Leaf-Node Focus:</b> This factory only understands the
 *       {@link FeatureAbstractClass.Builder}. It is responsible for
 *       turning "dead data" into "hardened entities."</li>
 *   <li><b>Nested Usage:</b> When building a Product Snapshot, the Product's
 *       own factory or builder will delegate to {@link #finalize(FeatureAbstractClass.Builder)}
 *       to ensure every nested feature passes the <b>Java 25 Constructor Prologue</b>.</li>
 *   <li><b>Purity:</b> No imports from Infrastructure (Jackson/Protobuf) or
 *       higher-level Aggregates (Product).</li>
 * </ul>
 */
public class FeatureEntityFactory {

    /**
     * Terminating function for Feature reconstitution.
     *
     * <p>Enforces the 2026 "Always-Valid" rule. If the builder contains
     * business violations (e.g. zero price), this call throws a
     * {@link DomainValidationException} immediately.</p>
     *
     * @param builder A pre-populated builder (Fixed, Scaling, or Tiered).
     * @return A hardened, immutable concrete Feature entity.
     */
    public static FeatureAbstractClass finalize(FeatureAbstractClass.Builder<?> builder) {
        // Triggers the Java 25 Prologue validation chain for the specific Feature.
        return builder.build();
    }
}

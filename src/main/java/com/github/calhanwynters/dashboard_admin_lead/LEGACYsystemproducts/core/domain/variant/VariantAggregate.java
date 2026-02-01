package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.variant;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.UuId;
import java.util.Collections;
import java.util.Set;

/**
 * Represents the immutable state of a Variant Collection Aggregate.
 */
public class VariantAggregate {
    private final int primaryKey;
    public final UuId variantId;
    private final UuId businessId;
    private final Set<Feature> features;

    // Package-private: Enforces use of VariantFactory
    VariantAggregate(int primaryKey, UuId variantId, UuId businessId, Set<Feature> features) {
        this.primaryKey = primaryKey;
        this.variantId = variantId;
        this.businessId = businessId;
        this.features = Collections.unmodifiableSet(features);
    }

    /**
     * Entry point for domain logic transitions.
     */
    public VariantBehavior act() {
        return new VariantBehavior(this);
    }

    public int getPrimaryKey() { return primaryKey; }
    public UuId getVariantId() { return variantId; }
    public UuId getBusinessId() { return businessId; }
    public Set<Feature> getFeatures() { return features; }
}

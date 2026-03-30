package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;

import java.util.*;

/**
 * High-performance policy engine using O(1) lookups to identify incompatible features.
 */
public final class FeatureCompatibilityPolicy {
    private final Map<TypedTrigger, Set<UuId>> rulesById;

    public FeatureCompatibilityPolicy(Set<IncompatibilityRule> rules) {
        Map<TypedTrigger, Set<UuId>> byId = new HashMap<>();

        for (IncompatibilityRule rule : rules) {
            // Re-mapping triggers to the forbidden Feature IDs
            TypedTrigger key = new TypedTrigger(rule.triggerUuId(), rule.triggerType());
            byId.computeIfAbsent(key, k -> new HashSet<>())
                    .add(rule.forbiddenFeatureUuId().value());
        }
        this.rulesById = Map.copyOf(byId);
    }

    public Set<UuId> getIncompatibleWith(Set<FeaturesAggregate> selectedFeatures, Set<TypesAggregate> selectedTypes) {
        Set<UuId> forbiddenIds = new HashSet<>();

        // 1. Check Feature-based triggers
        for (FeaturesAggregate feature : selectedFeatures) {
            UuId featureId = feature.getUuId().value();
            forbiddenIds.addAll(rulesById.getOrDefault(
                    TypedTrigger.feature(featureId), Collections.emptySet()));
        }

        // 2. Check Type-based triggers
        for (TypesAggregate type : selectedTypes) {
            UuId typeId = type.getUuId().value();
            forbiddenIds.addAll(rulesById.getOrDefault(
                    TypedTrigger.type(typeId), Collections.emptySet()));
        }

        return Collections.unmodifiableSet(forbiddenIds);
    }
}

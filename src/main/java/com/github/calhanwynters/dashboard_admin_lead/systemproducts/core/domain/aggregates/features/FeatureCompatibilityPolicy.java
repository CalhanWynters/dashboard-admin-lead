package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;

import java.util.*;

/**
 * High-performance policy engine using O(1) lookups to identify incompatible features.
 */
public final class FeatureCompatibilityPolicy {
    // Key is now TypedTrigger instead of raw UuId
    private final Map<TypedTrigger, Set<UuId>> rulesById;

    public FeatureCompatibilityPolicy(Set<IncompatibilityRule> rules) {
        Map<TypedTrigger, Set<UuId>> byId = new HashMap<>();

        for (IncompatibilityRule rule : rules) {
            // You'll need to determine if the rule trigger is a Type or Feature here
            // This might require a 'type' field on your IncompatibilityRule record
            TypedTrigger key = new TypedTrigger(rule.triggerUuId(), rule.triggerType());
            byId.computeIfAbsent(key, k -> new HashSet<>()).add(rule.forbiddenFeatureUuId().value());
        }
        this.rulesById = Map.copyOf(byId);
    }

    public Set<UuId> getIncompatibleWith(Set<FeaturesAggregate> selectedFeatures, Set<TypesAggregate> selectedTypes) {
        Set<UuId> forbiddenIds = new HashSet<>();

        // Query using Feature silo
        for (FeaturesAggregate feature : selectedFeatures) {
            forbiddenIds.addAll(rulesById.getOrDefault(
                    TypedTrigger.feature(feature.getFeaturesUuId().value()), Collections.emptySet()));
        }

        // Query using Type silo
        for (TypesAggregate type : selectedTypes) {
            forbiddenIds.addAll(rulesById.getOrDefault(
                    TypedTrigger.type(type.getTypesUuId().value()), Collections.emptySet()));
        }

        return Collections.unmodifiableSet(forbiddenIds);
    }
}

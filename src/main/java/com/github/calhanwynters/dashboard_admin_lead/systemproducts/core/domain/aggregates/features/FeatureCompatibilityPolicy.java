package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;

import java.util.*;

public final class FeatureCompatibilityPolicy {
    private final Map<TypedTrigger, Set<UuId>> rulesById;
    private final Map<FeatureLabel, Set<UuId>> rulesByTag; // New Tag Index

    public FeatureCompatibilityPolicy(Set<IncompatibilityRule> rules) {
        Map<TypedTrigger, Set<UuId>> byId = new HashMap<>();
        Map<FeatureLabel, Set<UuId>> byTag = new HashMap<>();

        for (IncompatibilityRule rule : rules) {
            // Index by ID if present
            if (rule.triggerUuId() != null) {
                TypedTrigger key = new TypedTrigger(rule.triggerUuId(), rule.triggerType());
                byId.computeIfAbsent(key, k -> new HashSet<>()).add(rule.forbiddenFeatureUuId().value());
            }
            // Index by Tag if present
            if (rule.triggerTag() != null) {
                byTag.computeIfAbsent(rule.triggerTag(), k -> new HashSet<>()).add(rule.forbiddenFeatureUuId().value());
            }
        }
        this.rulesById = Map.copyOf(byId);
        this.rulesByTag = Map.copyOf(byTag);
    }

    public Set<UuId> getIncompatibleWith(Set<FeaturesAggregate> selectedFeatures, Set<TypesAggregate> selectedTypes) {
        Set<UuId> forbiddenIds = new HashSet<>();

        // 1. Resolve ID-based triggers (Features & Types)
        for (FeaturesAggregate f : selectedFeatures) {
            forbiddenIds.addAll(rulesById.getOrDefault(TypedTrigger.feature(f.getUuId().value()), Set.of()));

            // 2. Resolve Tag-based triggers for this feature
            forbiddenIds.addAll(rulesByTag.getOrDefault(f.getCompatibilityTag(), Set.of()));
        }

        for (TypesAggregate t : selectedTypes) {
            forbiddenIds.addAll(rulesById.getOrDefault(TypedTrigger.type(t.getUuId().value()), Set.of()));
            // Note: If Types gain a compatibilityTag later, add a rulesByTypeTag map here.
        }

        return Collections.unmodifiableSet(forbiddenIds);
    }
}

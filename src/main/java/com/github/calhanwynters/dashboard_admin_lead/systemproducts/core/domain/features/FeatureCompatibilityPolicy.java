package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.FeatureLabel;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesAggregate;

import java.util.*;

/**
 * High-performance policy engine using O(1) lookups to identify incompatible features.
 */
public final class FeatureCompatibilityPolicy {
    private final Map<UuId, Set<UuId>> rulesById;
    private final Map<FeatureLabel, Set<UuId>> rulesByTag;

    public FeatureCompatibilityPolicy(Set<IncompatibilityRule> rules) {
        Map<UuId, Set<UuId>> byId = new HashMap<>();
        Map<FeatureLabel, Set<UuId>> byTag = new HashMap<>();

        for (IncompatibilityRule rule : rules) {
            UuId forbidden = rule.forbiddenFeatureUuId().value();

            if (rule.triggerUuId() != null) {
                byId.computeIfAbsent(rule.triggerUuId(), k -> new HashSet<>()).add(forbidden);
            }

            if (rule.triggerTag() != null) {
                byTag.computeIfAbsent(rule.triggerTag(), k -> new HashSet<>()).add(forbidden);
            }
        }

        // Defensive immutability
        this.rulesById = Map.copyOf(byId);
        this.rulesByTag = Map.copyOf(byTag);
    }

    public Set<UuId> getIncompatibleWith(Set<FeaturesAggregate> selectedFeatures, Set<TypesAggregate> selectedTypes) {
        Set<UuId> forbiddenIds = new HashSet<>();

        // 1. Process Selected Features (ID + Tag triggers)
        for (FeaturesAggregate feature : selectedFeatures) {
            forbiddenIds.addAll(rulesById.getOrDefault(feature.getFeaturesUuId().value(), Collections.emptySet()));

            // DomainGuard in Aggregate ensures tag is never null
            forbiddenIds.addAll(rulesByTag.getOrDefault(feature.getCompatibilityTag(), Collections.emptySet()));
        }

        // 2. Process Selected Types (ID triggers)
        for (TypesAggregate type : selectedTypes) {
            forbiddenIds.addAll(rulesById.getOrDefault(type.getTypesUuId().value(), Collections.emptySet()));
        }

        return Collections.unmodifiableSet(forbiddenIds);
    }
}

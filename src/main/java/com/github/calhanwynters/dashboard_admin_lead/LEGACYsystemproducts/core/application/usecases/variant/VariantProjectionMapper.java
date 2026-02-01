package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.application.usecases.variant;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.variant.VariantAggregate;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.variant.Feature;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeatureCompatibilityPolicy;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.IncompatibilityRule;

import java.util.*;
import java.util.stream.Collectors;

public class VariantProjectionMapper {

    public static VariantProjectionDTO toDTO(VariantAggregate collection, FeatureCompatibilityPolicy policy) {
        if (collection == null) return null;

        // 1. Map Features
        Set<VariantProjectionDTO.FeatureDTO> featureDTOs = collection.getFeatures()
                .stream()
                .map(VariantProjectionMapper::mapFeature)
                .collect(Collectors.toSet());

        // 2. Pre-calculate the Incompatibility Lookup Map
        Map<String, Set<String>> lookup = new HashMap<>();
        for (IncompatibilityRule rule : policy.rules()) {
            // Handle ID-based triggers
            if (rule.triggerUuId() != null) {
                lookup.computeIfAbsent(rule.triggerUuId().value(), k -> new HashSet<>())
                        .add(rule.forbiddenFeatureUuId().value());
            }
            // Handle Tag-based triggers
            if (rule.triggerTag() != null) {
                lookup.computeIfAbsent(rule.triggerTag().value(), k -> new HashSet<>())
                        .add(rule.forbiddenFeatureUuId().value());
            }
        }

        return new VariantProjectionDTO(
                collection.getVariantId().value(),
                collection.getBusinessId().value(),
                featureDTOs,
                lookup,
                collection.act().size()
        );
    }

    private static VariantProjectionDTO.FeatureDTO mapFeature(Feature feature) {
        return new VariantProjectionDTO.FeatureDTO(
                feature.featureUuId().value(),
                feature.featureName().value(),
                feature.featureDescription().text(),
                feature.compatibilityTag().value()
        );
    }
}

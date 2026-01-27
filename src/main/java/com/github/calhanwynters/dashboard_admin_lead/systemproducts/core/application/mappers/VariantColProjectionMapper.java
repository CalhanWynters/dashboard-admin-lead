package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.mappers;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.dto.VariantColProjectionDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant.VariantCollection;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant.Feature;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant.FeatureCompatibilityPolicy;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant.IncompatibilityRule;

import java.util.*;
import java.util.stream.Collectors;

public class VariantColProjectionMapper {

    public static VariantColProjectionDTO toDTO(VariantCollection collection, FeatureCompatibilityPolicy policy) {
        if (collection == null) return null;

        // 1. Map Features
        Set<VariantColProjectionDTO.FeatureDTO> featureDTOs = collection.getFeatures()
                .stream()
                .map(VariantColProjectionMapper::mapFeature)
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

        return new VariantColProjectionDTO(
                collection.getVariantColId().value(),
                collection.getBusinessId().value(),
                featureDTOs,
                lookup,
                collection.act().size()
        );
    }

    private static VariantColProjectionDTO.FeatureDTO mapFeature(Feature feature) {
        return new VariantColProjectionDTO.FeatureDTO(
                feature.featureUuId().value(),
                feature.featureName().value(),
                feature.featureDescription().text(),
                feature.compatibilityTag().value()
        );
    }
}

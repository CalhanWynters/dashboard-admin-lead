package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.dto;

import java.util.Map;
import java.util.Set;

public record VariantColProjectionDTO(
        String variantColId,
        String businessId,
        Set<FeatureDTO> features,
        // Key: Trigger (ID or Tag), Value: Set of Feature IDs to disable
        Map<String, Set<String>> incompatibilityLookup,
        int totalFeatures
) {
    public record FeatureDTO(
            String id,
            String name,
            String description,
            String compatibilityTag
    ) {}
}

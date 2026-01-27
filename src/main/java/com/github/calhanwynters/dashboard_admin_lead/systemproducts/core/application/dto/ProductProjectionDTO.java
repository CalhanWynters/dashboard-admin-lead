package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.dto;

import java.util.Set;

/**
 * A read-optimized snapshot of the Product Aggregate for 2026 Dashboards.
 */
public record ProductProjectionDTO(
        String productUuId,
        String businessId,
        String name,
        String category,
        String description,
        String status,
        int schemaVersion,
        String lastModified,      // Used for 2026 Temporal Optimistic Locking
        String galleryColId,
        String typeColId,         // Nullable in UI
        String variantColId,      // Nullable in UI

        // Physical Specs (Flattened from VOs)
        PhysicalSpecsDTO specs,

        // Rules Metadata
        Set<IncompatibilityRuleDTO> rules
) {
    public record PhysicalSpecsDTO(
            String length,
            String width,
            String height,
            String dimensionUnit,
            String weightAmount,
            String weightUnit,
            String careInstructions
    ) {}

    public record IncompatibilityRuleDTO(
            String triggerUuId,
            String triggerTag,
            String forbiddenFeatureUuId
    ) {}
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant.VariantCollection;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant.Feature;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;

import java.util.Optional;
import java.util.Set;

/**
 * CQRS Read-Side Repository for Variant Collections.
 * Optimized for state retrieval in 2026 clean architecture standards.
 */
public interface VariantColQueryRepository {

    /**
     * Retrieves the current state of a VariantCollection by its Domain UUID.
     * Essential for the Read-side of CQRS to display current configurations.
     */
    Optional<VariantCollection> findById(UuId variantColId);

    /**
     * Retrieves all collections belonging to a specific business.
     */
    Set<VariantCollection> findAllByBusinessId(UuId businessId);

    /**
     * Finds collections that contain a specific Feature.
     * Leverages the Feature Value Object from your domain.
     */
    Set<VariantCollection> findByFeature(Feature feature);

    /**
     * Checks if a collection exists for a business without loading the full aggregate.
     * Useful for validation before executing Commands.
     */
    boolean existsByBusinessId(UuId businessId);

    /**
     * Returns the total count of features across a specific collection.
     * Maps to the size() method in your VariantColBehavior.
     */
    int countFeaturesInCollection(UuId variantColId);
}

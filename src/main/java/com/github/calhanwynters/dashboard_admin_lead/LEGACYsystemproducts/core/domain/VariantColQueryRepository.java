package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.variant.VariantAggregate;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.variant.VariantCollectionAggregate;

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
    Optional<VariantCollectionAggregate> findById(UuId variantColId);

    /**
     * Retrieves all collections belonging to a specific business.
     */
    Set<VariantCollectionAggregate> findAllByBusinessId(UuId businessId);

    /**
     * Finds collections that contain a specific Variant.
     * Leverages the Variant Value Object from your domain.
     */
    Set<VariantCollectionAggregate> findByVariant(VariantAggregate variant);

    /**
     * Checks if a collection exists for a business without loading the full aggregate.
     * Useful for validation before executing Commands.
     */
    boolean existsByBusinessId(UuId businessId);

    /**
     * Returns the total count of variants across a specific collection.
     * Maps to the size() method in your VariantColBehavior.
     */
    int countVariantsInCollection(UuId variantColId);

}

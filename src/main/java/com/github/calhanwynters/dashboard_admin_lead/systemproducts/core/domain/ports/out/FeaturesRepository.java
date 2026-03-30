package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureBusinessUuId;

import java.util.Optional;
import java.util.List;

/**
 * Outbound Port for Features (2026 Edition).
 * Orchestrates persistence for the refactored FeaturesAggregate.
 */
public interface FeaturesRepository {

    /**
     * Primary lookup by the Domain Technical UUID.
     */
    Optional<FeaturesAggregate> findByUuId(FeatureUuId uuId);

    /**
     * Business lookup used for external integrations and uniqueness checks.
     */
    Optional<FeaturesAggregate> findByBusinessUuId(FeatureBusinessUuId businessUuId);

    /**
     * Saves the aggregate state and returns the reconstituted instance.
     * Essential for capturing DB-generated IDs or Optimistic Locking versions.
     */
    FeaturesAggregate save(FeaturesAggregate aggregate);

    /**
     * Retrieves all non-archived and non-deleted features.
     */
    List<FeaturesAggregate> findAllActive();

    /**
     * Permanent removal from the storage layer.
     */
    void hardDelete(FeatureUuId uuId);
}

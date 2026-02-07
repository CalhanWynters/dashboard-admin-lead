package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesAggregate;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;

import java.util.Optional;
import java.util.List;

/**
 * Domain-driven Repository interface for managing Feature Aggregates.
 * Defined in the Domain Layer to decouple business logic from persistence infrastructure.
 */
public interface FeaturesRepository {

    // --- Retrieval ---

    /**
     * Reconstitutes the aggregate by its internal technical UUID.
     */
    Optional<FeaturesAggregate> findByUuId(FeatureUuId uuId);

    /**
     * Reconstitutes the aggregate by its business-facing UUID.
     */
    Optional<FeaturesAggregate> findByBusinessUuId(FeatureBusinessUuId businessUuId);

    /**
     * Retrieves all features. In production, consider adding Pagination parameters.
     */
    List<FeaturesAggregate> findAll();

    // --- Persistence ---

    /**
     * Persists a new or modified FeaturesAggregate.
     * Implementations should handle Event Dispatching or Unit of Work patterns.
     */
    void save(FeaturesAggregate aggregate);

    /**
     * Removes the aggregate from the system.
     * Note: Soft-deletion is usually handled via .save() after calling aggregate.softDelete().
     */
    void delete(FeaturesAggregate aggregate);

    // --- Validation Helpers ---

    /**
     * Checks for existence to support Domain Guards in Application Services.
     */
    boolean existsByBusinessUuId(FeatureBusinessUuId businessUuId);
}

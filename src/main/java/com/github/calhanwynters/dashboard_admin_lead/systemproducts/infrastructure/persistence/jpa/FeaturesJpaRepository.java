package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.FeaturesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Features.
 * Handles low-level SQL execution for the Postgresql Adapter.
 */
@Repository
public interface FeaturesJpaRepository extends JpaRepository<FeaturesEntity, Long> {

    // Primary UUID Lookup
    Optional<FeaturesEntity> findByUuid(UUID uuid);

    // Business UUID Lookup
    Optional<FeaturesEntity> findByBusinessUuid(UUID businessUuid);

    // Query for Active Features (Non-archived and Non-deleted)
    @Query("SELECT f FROM FeaturesEntity f WHERE f.archived = false AND f.softDeleted = false")
    List<FeaturesEntity> findAllActive();

    // Deletion by UUID
    void deleteByUuid(UUID uuid);
}

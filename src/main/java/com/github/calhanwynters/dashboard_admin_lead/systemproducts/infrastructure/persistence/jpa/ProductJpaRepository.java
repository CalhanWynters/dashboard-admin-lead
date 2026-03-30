package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByUuid(UUID uuid);

    Optional<ProductEntity> findByBusinessUuid(UUID businessUuid);

    // Reference Lookup for dependency validation
    List<ProductEntity> findAllByGalleryUuid(UUID galleryUuid);

    // Status filtering for Admin Dashboards
    List<ProductEntity> findAllByStatus(String status);

    @Query("SELECT p FROM ProductEntity p WHERE p.archived = false AND p.softDeleted = false")
    List<ProductEntity> findAllActive();

    void deleteByUuid(UUID uuid);
}

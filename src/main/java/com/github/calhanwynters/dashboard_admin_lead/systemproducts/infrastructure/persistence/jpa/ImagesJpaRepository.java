package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.ImagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImagesJpaRepository extends JpaRepository<ImagesEntity, Long> {

    Optional<ImagesEntity> findByUuid(UUID uuid);

    Optional<ImagesEntity> findByBusinessUuid(UUID businessUuid);

    // Support for deduplication/integrity checks via URL
    Optional<ImagesEntity> findByUrl(String url);

    @Query("SELECT i FROM ImagesEntity i WHERE i.archived = false AND i.softDeleted = false")
    List<ImagesEntity> findAllActive();

    void deleteByUuid(UUID uuid);
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.GalleryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GalleryJpaRepository extends JpaRepository<GalleryEntity, Long> {

    Optional<GalleryEntity> findByUuid(UUID uuid);

    Optional<GalleryEntity> findByBusinessUuid(UUID businessUuid);

    // Relationship Query: Finds galleries containing a specific image UUID
    // This assumes a Join Table or a collection of UUIDs in the Entity
    @Query("SELECT g FROM GalleryEntity g JOIN g.imageUuids i WHERE i = :imageUuid")
    List<GalleryEntity> findAllByImageUuid(@Param("imageUuid") UUID imageUuid);

    @Query("SELECT g FROM GalleryEntity g WHERE g.archived = false AND g.softDeleted = false")
    List<GalleryEntity> findAllActive();

    @Query("SELECT g FROM GalleryEntity g WHERE g.status = 'PUBLIC' AND g.archived = false")
    List<GalleryEntity> findAllPublic();

    void deleteByUuid(UUID uuid);
}

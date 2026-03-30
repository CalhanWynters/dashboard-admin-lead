package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.VariantsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VariantsJpaRepository extends JpaRepository<VariantsEntity, Long> {

    Optional<VariantsEntity> findByUuid(UUID uuid);

    Optional<VariantsEntity> findByBusinessUuid(UUID businessUuid);

    // FIXED: Changed join from 'featureUuids' to 'assignedFeatureUuids'
    @Query("SELECT v FROM VariantsEntity v JOIN v.assignedFeatureUuids f WHERE f = :featureUuid")
    List<VariantsEntity> findAllByFeatureUuid(@Param("featureUuid") UUID featureUuid);

    @Query("SELECT v FROM VariantsEntity v WHERE v.archived = false AND v.softDeleted = false")
    List<VariantsEntity> findAllActive();

    void deleteByUuid(UUID uuid);
}

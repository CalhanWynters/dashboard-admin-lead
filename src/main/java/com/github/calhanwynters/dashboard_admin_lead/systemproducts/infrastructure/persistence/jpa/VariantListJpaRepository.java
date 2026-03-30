package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.VariantListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VariantListJpaRepository extends JpaRepository<VariantListEntity, Long> {

    Optional<VariantListEntity> findByUuid(UUID uuid);

    Optional<VariantListEntity> findByBusinessUuid(UUID businessUuid);

    @Query("SELECT v FROM VariantListEntity v JOIN v.variantUuids t WHERE t = :variantUuid")
    List<VariantListEntity> findAllByContainsVariantUuid(@Param("variantUuid") UUID variantUuid);

    @Query("SELECT v FROM VariantListEntity v WHERE v.archived = false AND v.softDeleted = false")
    List<VariantListEntity> findAllActive();

    void deleteByUuid(UUID uuid);
}

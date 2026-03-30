package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.TypeListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TypeListJpaRepository extends JpaRepository<TypeListEntity, Long> {

    Optional<TypeListEntity> findByUuid(UUID uuid);

    Optional<TypeListEntity> findByBusinessUuid(UUID businessUuid);

    @Query("SELECT tl FROM TypeListEntity tl JOIN tl.typeUuIds t WHERE t = :typeUuid")
    List<TypeListEntity> findAllByContainsTypeUuid(@Param("typeUuid") UUID typeUuid);

    @Query("SELECT tl FROM TypeListEntity tl WHERE tl.archived = false AND tl.softDeleted = false")
    List<TypeListEntity> findAllActive();

    void deleteByUuid(UUID uuid);
}

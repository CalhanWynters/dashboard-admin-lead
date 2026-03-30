package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.TypesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TypesJpaRepository extends JpaRepository<TypesEntity, Long> {

    Optional<TypesEntity> findByUuid(UUID uuid);

    Optional<TypesEntity> findByBusinessUuid(UUID businessUuid);

    // Support for UI lookups and name uniqueness checks
    Optional<TypesEntity> findByName(String name);

    @Query("SELECT t FROM TypesEntity t WHERE t.archived = false AND t.softDeleted = false")
    List<TypesEntity> findAllActive();

    void deleteByUuid(UUID uuid);
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PricingStrategyType;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.PriceListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PriceListJpaRepository extends JpaRepository<PriceListEntity, Long> {

    Optional<PriceListEntity> findByUuid(UUID uuid);

    Optional<PriceListEntity> findByBusinessUuid(UUID businessUuid);

    // Filter by the slug string stored in the DB
    @Query("SELECT p FROM PriceListEntity p WHERE p.strategySlug = :strategy")
    List<PriceListEntity> findByStrategySlug(@Param("strategy") String strategy);

    @Query("SELECT p FROM PriceListEntity p WHERE p.archived = false AND p.softDeleted = false")
    List<PriceListEntity> findAllActive();

    // Mapping version_count (from @Version) to the business version query
    Optional<PriceListEntity> findByBusinessUuidAndVersion(UUID businessUuid, Integer version);

    void deleteByUuid(UUID uuid);
}

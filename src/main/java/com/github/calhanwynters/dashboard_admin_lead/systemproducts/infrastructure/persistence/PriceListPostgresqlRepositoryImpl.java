package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListVersion;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PricingStrategyType;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.PriceListRepository;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs.PriceListMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.PriceListEntity;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa.PriceListJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Persistence Adapter for Price Lists.
 * Bridges the Domain Port to PostgreSQL using MapStruct and Spring Data JPA.
 */
@Repository
public class PriceListPostgresqlRepositoryImpl implements PriceListRepository {

    private final PriceListJpaRepository jpaRepository;
    private final PriceListMapper mapper;

    public PriceListPostgresqlRepositoryImpl(PriceListJpaRepository jpaRepository, PriceListMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PriceListAggregate> findByUuId(PriceListUuId priceListUuId) {
        return jpaRepository.findByUuid(UUID.fromString(priceListUuId.value().value()))
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PriceListAggregate> findByBusinessUuId(PriceListBusinessUuId businessUuId) {
        return jpaRepository.findByBusinessUuid(UUID.fromString(businessUuId.value().value()))
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional
    public PriceListAggregate save(PriceListAggregate aggregate) {
        // Translate Domain -> Infra
        PriceListEntity entity = mapper.toEntity(aggregate);

        // Persist to DB
        PriceListEntity savedEntity = jpaRepository.save(entity);

        // Reconstitute Infra -> Domain (to pick up optLockVer and DB timestamps)
        return mapper.toAggregate(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceListAggregate> findByStrategy(PricingStrategyType strategy) {
        return jpaRepository.findByStrategySlug(strategy.name()).stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceListAggregate> findAllActive() {
        return jpaRepository.findAllActive().stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PriceListAggregate> findByBusinessUuIdAndVersion(
            PriceListBusinessUuId businessUuId,
            PriceListVersion version) {

        UUID bId = UUID.fromString(businessUuId.value().value());
        return jpaRepository.findByBusinessUuidAndVersion(bId, version.value().value())
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional
    public void hardDelete(PriceListUuId priceListUuId) {
        jpaRepository.deleteByUuid(UUID.fromString(priceListUuId.value().value()));
    }
}

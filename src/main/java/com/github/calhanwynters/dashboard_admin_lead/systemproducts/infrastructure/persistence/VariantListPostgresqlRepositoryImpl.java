package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.VariantListRepository;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs.VariantListMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.VariantListEntity;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa.VariantListJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Persistence Adapter for Variant Lists.
 * Manages the collection-based membership of Product Variants.
 */
@Repository
public class VariantListPostgresqlRepositoryImpl implements VariantListRepository {

    private final VariantListJpaRepository jpaRepository;
    private final VariantListMapper mapper;

    public VariantListPostgresqlRepositoryImpl(VariantListJpaRepository jpaRepository, VariantListMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VariantListAggregate> findByUuId(VariantListUuId variantListUuId) {
        return jpaRepository.findByUuid(UUID.fromString(variantListUuId.value().value()))
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VariantListAggregate> findByBusinessUuId(VariantListBusinessUuId businessUuId) {
        return jpaRepository.findByBusinessUuid(UUID.fromString(businessUuId.value().value()))
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional
    public VariantListAggregate save(VariantListAggregate aggregate) {
        VariantListEntity entity = mapper.toEntity(aggregate);
        VariantListEntity savedEntity = jpaRepository.save(entity);
        return mapper.toAggregate(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VariantListAggregate> findAllByContainsVariant(VariantsUuId variantUuId) {
        UUID searchId = UUID.fromString(variantUuId.value().value());
        return jpaRepository.findAllByContainsVariantUuid(searchId).stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VariantListAggregate> findAllActive() {
        return jpaRepository.findAllActive().stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void hardDelete(VariantListAggregate aggregate) {
        // Extract the raw UUID from the hardened Domain Wrapper
        UUID uuid = UUID.fromString(aggregate.getUuId().value().value());
        jpaRepository.deleteByUuid(uuid);
    }
}

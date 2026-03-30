package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.VariantsRepository;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs.VariantsMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.VariantsEntity;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa.VariantsJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Persistence Adapter for Individual Variants (2026 Edition).
 * Orchestrates the persistence of variant-specific features and audit trails.
 */
@Repository
public class VariantsPostgresqlRepositoryImpl implements VariantsRepository {

    private final VariantsJpaRepository jpaRepository;
    private final VariantsMapper mapper;

    public VariantsPostgresqlRepositoryImpl(VariantsJpaRepository jpaRepository, VariantsMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VariantsAggregate> findByUuId(VariantsUuId variantsUuId) {
        return jpaRepository.findByUuid(UUID.fromString(variantsUuId.value().value()))
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VariantsAggregate> findByBusinessUuId(VariantsBusinessUuId businessUuId) {
        return jpaRepository.findByBusinessUuid(UUID.fromString(businessUuId.value().value()))
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional
    public VariantsAggregate save(VariantsAggregate aggregate) {
        VariantsEntity entity = mapper.toEntity(aggregate);
        VariantsEntity savedEntity = jpaRepository.save(entity);
        return mapper.toAggregate(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VariantsAggregate> findAllByFeatureUuId(FeatureUuId featureUuId) {
        UUID searchId = UUID.fromString(featureUuId.value().value());
        return jpaRepository.findAllByFeatureUuid(searchId).stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VariantsAggregate> findAllActive() {
        return jpaRepository.findAllActive().stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void hardDelete(VariantsUuId variantsUuId) {
        jpaRepository.deleteByUuid(UUID.fromString(variantsUuId.value().value()));
    }
}

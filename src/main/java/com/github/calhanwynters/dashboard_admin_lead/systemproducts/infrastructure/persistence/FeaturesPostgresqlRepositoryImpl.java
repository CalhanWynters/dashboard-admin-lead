package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.FeaturesRepository;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs.FeaturesMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.FeaturesEntity;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa.FeaturesJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Persistence Adapter for Features (2026 Edition).
 * Pure Java implementation with explicit constructor injection.
 */
@Repository
public class FeaturesPostgresqlRepositoryImpl implements FeaturesRepository {

    private final FeaturesJpaRepository jpaRepository;
    private final FeaturesMapper mapper;

    public FeaturesPostgresqlRepositoryImpl(FeaturesJpaRepository jpaRepository, FeaturesMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FeaturesAggregate> findByUuId(FeatureUuId uuId) {
        return jpaRepository.findByUuid(UUID.fromString(uuId.value().value()))
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FeaturesAggregate> findByBusinessUuId(FeatureBusinessUuId businessUuId) {
        return jpaRepository.findByBusinessUuid(UUID.fromString(businessUuId.value().value()))
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional
    public FeaturesAggregate save(FeaturesAggregate aggregate) {
        FeaturesEntity entity = mapper.toEntity(aggregate);
        FeaturesEntity savedEntity = jpaRepository.save(entity);
        return mapper.toAggregate(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeaturesAggregate> findAllActive() {
        return jpaRepository.findAllActive().stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void hardDelete(FeatureUuId uuId) {
        jpaRepository.deleteByUuid(UUID.fromString(uuId.value().value()));
    }
}

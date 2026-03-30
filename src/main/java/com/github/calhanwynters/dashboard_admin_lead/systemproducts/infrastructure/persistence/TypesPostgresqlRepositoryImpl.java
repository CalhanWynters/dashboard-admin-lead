package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesName;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.TypesRepository;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs.TypesMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.TypesEntity;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa.TypesJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Persistence Adapter for Product Types (2026 Edition).
 * Orchestrates the persistence of classification metadata and physical specs.
 */
@Repository
public class TypesPostgresqlRepositoryImpl implements TypesRepository {

    private final TypesJpaRepository jpaRepository;
    private final TypesMapper mapper;

    public TypesPostgresqlRepositoryImpl(TypesJpaRepository jpaRepository, TypesMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TypesAggregate> findByUuId(TypesUuId typesUuId) {
        return jpaRepository.findByUuid(UUID.fromString(typesUuId.value().value()))
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TypesAggregate> findByBusinessUuId(TypesBusinessUuId businessUuId) {
        return jpaRepository.findByBusinessUuid(UUID.fromString(businessUuId.value().value()))
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional
    public TypesAggregate save(TypesAggregate aggregate) {
        TypesEntity entity = mapper.toEntity(aggregate);
        TypesEntity savedEntity = jpaRepository.save(entity);
        return mapper.toAggregate(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TypesAggregate> findByName(TypesName typesName) {
        return jpaRepository.findByName(typesName.value().value())
                .map(mapper::toAggregate);
    }


    @Override
    @Transactional(readOnly = true)
    public List<TypesAggregate> findAllActive() {
        return jpaRepository.findAllActive().stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void hardDelete(TypesUuId typesUuId) {
        jpaRepository.deleteByUuid(UUID.fromString(typesUuId.value().value()));
    }
}

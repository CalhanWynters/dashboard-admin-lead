package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.TypeListRepository;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs.TypeListMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.TypeListEntity;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa.TypeListJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Persistence Adapter for Type Lists.
 * Manages the collection-based membership of Product Types.
 */
@Repository
public class TypeListPostgresqlRepositoryImpl implements TypeListRepository {

    private final TypeListJpaRepository jpaRepository;
    private final TypeListMapper mapper;

    public TypeListPostgresqlRepositoryImpl(TypeListJpaRepository jpaRepository, TypeListMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TypeListAggregate> findByUuId(TypeListUuId typeListUuId) {
        return jpaRepository.findByUuid(UUID.fromString(typeListUuId.value().value()))
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TypeListAggregate> findByBusinessUuId(TypeListBusinessUuId businessUuId) {
        return jpaRepository.findByBusinessUuid(UUID.fromString(businessUuId.value().value()))
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional
    public TypeListAggregate save(TypeListAggregate aggregate) {
        TypeListEntity entity = mapper.toEntity(aggregate);
        TypeListEntity savedEntity = jpaRepository.save(entity);
        return mapper.toAggregate(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TypeListAggregate> findAllByContainsType(TypesUuId typeUuId) {
        UUID searchId = UUID.fromString(typeUuId.value().value());
        return jpaRepository.findAllByContainsTypeUuid(searchId).stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TypeListAggregate> findAllActive() {
        return jpaRepository.findAllActive().stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void hardDelete(TypeListUuId typeListUuId) {
        jpaRepository.deleteByUuid(UUID.fromString(typeListUuId.value().value()));
    }
}

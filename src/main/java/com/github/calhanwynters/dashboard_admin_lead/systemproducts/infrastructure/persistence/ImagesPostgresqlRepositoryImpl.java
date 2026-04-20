package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImageAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImagesBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUrl;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.ImagesRepository;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs.ImagesMapStruct;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.ImagesEntity;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa.ImagesJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ImagesPostgresqlRepositoryImpl implements ImagesRepository {

    private final ImagesJpaRepository jpaRepository;
    private final ImagesMapStruct mapper;

    public ImagesPostgresqlRepositoryImpl(ImagesJpaRepository jpaRepository, ImagesMapStruct mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ImageAggregate> findByUuId(ImageUuId imagesUuId) {
        return jpaRepository.findByUuid(UUID.fromString(imagesUuId.value().value()))
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ImageAggregate> findByBusinessUuId(ImagesBusinessUuId businessUuId) {
        return jpaRepository.findByBusinessUuid(UUID.fromString(businessUuId.value().value()))
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional
    public ImageAggregate save(ImageAggregate aggregate) {
        ImagesEntity entity = mapper.toEntity(aggregate);
        ImagesEntity savedEntity = jpaRepository.save(entity);
        return mapper.toAggregate(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ImageAggregate> findByUrl(ImageUrl imageUrl) {
        return jpaRepository.findByUrl(imageUrl.value())
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageAggregate> findAllActive() {
        return jpaRepository.findAllActive().stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void hardDelete(ImageUuId imagesUuId) {
        jpaRepository.deleteByUuid(UUID.fromString(imagesUuId.value().value()));
    }
}

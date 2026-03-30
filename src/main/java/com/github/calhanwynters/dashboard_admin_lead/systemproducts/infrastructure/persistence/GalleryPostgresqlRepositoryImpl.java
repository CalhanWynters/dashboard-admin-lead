package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.GalleryRepository;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs.GalleryMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.GalleryEntity;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.jpa.GalleryJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class GalleryPostgresqlRepositoryImpl implements GalleryRepository {

    private final GalleryJpaRepository jpaRepository;
    private final GalleryMapper mapper;

    public GalleryPostgresqlRepositoryImpl(GalleryJpaRepository jpaRepository, GalleryMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GalleryAggregate> findByUuId(GalleryUuId galleryUuId) {
        return jpaRepository.findByUuid(UUID.fromString(galleryUuId.value().value()))
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GalleryAggregate> findByBusinessUuId(GalleryBusinessUuId businessUuId) {
        return jpaRepository.findByBusinessUuid(UUID.fromString(businessUuId.value().value()))
                .map(mapper::toAggregate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GalleryAggregate> findAllByImageUuId(ImageUuId imageUuId) {
        UUID searchId = UUID.fromString(imageUuId.value().value());
        return jpaRepository.findAllByImageUuid(searchId).stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GalleryAggregate save(GalleryAggregate aggregate) {
        GalleryEntity entity = mapper.toEntity(aggregate);
        GalleryEntity savedEntity = jpaRepository.save(entity);
        return mapper.toAggregate(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GalleryAggregate> findAllPublic() {
        return jpaRepository.findAllPublic().stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GalleryAggregate> findAllActive() {
        return jpaRepository.findAllActive().stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void hardDelete(GalleryUuId galleryUuId) {
        jpaRepository.deleteByUuid(UUID.fromString(galleryUuId.value().value()));
    }
}

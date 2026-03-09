package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.GalleryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.*;

@Mapper(componentModel = "spring")
public interface GalleryMapper {

    @Mapping(target = "galleryId", source = "id", qualifiedByName = "toGalleryId")
    @Mapping(target = "galleryUuId", source = "uuid", qualifiedByName = "toGalleryUuId")
    @Mapping(target = "galleryBusinessUuId", source = "businessUuid", qualifiedByName = "toGalleryBusinessUuId")
    @Mapping(target = "imageUuIds", source = "imageUuids", qualifiedByName = "toImageUuIdList")
    @Mapping(target = "productBooleans", source = ".", qualifiedByName = "toProductBooleans")
    @Mapping(target = "auditMetadata", source = ".", qualifiedByName = "toAuditMetadata")
    GalleryAggregate toAggregate(GalleryEntity entity);

    @Mapping(target = "id", source = "galleryId.value.id")
    @Mapping(target = "uuid", source = "galleryUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "businessUuid", source = "galleryBusinessUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "imageUuids", source = "imageUuIds", qualifiedByName = "fromImageUuIdList")
    @Mapping(target = "archived", source = "productBooleans.archived")
    @Mapping(target = "softDeleted", source = "productBooleans.softDeleted")
    @Mapping(target = "createdAt", source = "auditMetadata.createdAt.value")
    @Mapping(target = "lastModifiedAt", source = "auditMetadata.lastModified.value")
    @Mapping(target = "lastModifiedBy", source = "auditMetadata.lastModifiedBy.identity")
    GalleryEntity toEntity(GalleryAggregate aggregate);

    // --- Helper Methods ---

    @Named("toGalleryId")
    default GalleryId toGalleryId(Long id) { return id != null ? new GalleryId(PkId.of(id)) : null; }

    @Named("toGalleryUuId")
    default GalleryUuId toGalleryUuId(UUID uuid) {
        return uuid != null ? new GalleryUuId(UuId.fromString(uuid.toString())) : null;
    }

    @Named("toGalleryBusinessUuId")
    default GalleryBusinessUuId toGalleryBusinessUuId(UUID uuid) {
        return uuid != null ? new GalleryBusinessUuId(UuId.fromString(uuid.toString())) : null;
    }

    @Named("toImageUuIdList")
    default List<ImageUuId> toImageUuIdList(List<UUID> uuids) {
        if (uuids == null) return Collections.emptyList();
        return uuids.stream()
                .map(u -> new ImageUuId(UuId.fromString(u.toString())))
                .collect(Collectors.toList());
    }

    @Named("fromImageUuIdList")
    default List<UUID> fromImageUuIdList(List<ImageUuId> imageUuIds) {
        if (imageUuIds == null) return Collections.emptyList();
        return imageUuIds.stream()
                .map(i -> UUID.fromString(i.value().value()))
                .collect(Collectors.toList());
    }

    @Named("toProductBooleans")
    default ProductBooleans toProductBooleans(GalleryEntity entity) {
        return new ProductBooleans(entity.isArchived(), entity.isSoftDeleted());
    }

    @Named("toAuditMetadata")
    default AuditMetadata toAuditMetadata(GalleryEntity entity) {
        return AuditMetadata.reconstitute(
                new CreatedAt(entity.getCreatedAt()),
                new LastModified(entity.getLastModifiedAt()),
                new Actor(entity.getLastModifiedBy(), Collections.emptySet())
        );
    }

    @Named("stringToUuid")
    default java.util.UUID stringToUuid(String value) {
        return value != null ? java.util.UUID.fromString(value) : null;
    }
}

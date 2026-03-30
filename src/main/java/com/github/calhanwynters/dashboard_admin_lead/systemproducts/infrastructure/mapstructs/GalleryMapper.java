package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.GalleryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

@Mapper(componentModel = "spring")
public interface GalleryMapper {

    // TO AGGREGATE
    @Mapping(target = "id", source = "id", qualifiedByName = "toGalleryId")
    @Mapping(target = "uuId", source = "uuid", qualifiedByName = "toGalleryUuId")
    @Mapping(target = "businessUuId", source = "businessUuid", qualifiedByName = "toBusinessUuId")
    @Mapping(target = "public", source = "isPublic")
    @Mapping(target = "imageUuIds", source = "imageUuids", qualifiedByName = "toImageUuIdList")
    @Mapping(target = "auditMetadata", source = ".", qualifiedByName = "toAuditMetadata")
    @Mapping(target = "lifecycleState", source = ".", qualifiedByName = "toLifecycleState")
    @Mapping(target = "optLockVer", source = "version")
    @Mapping(target = "schemaVer", constant = "1")
    GalleryAggregate toAggregate(GalleryEntity entity);

    // TO ENTITY
    @Mapping(target = "id", source = "id.value.id")
    @Mapping(target = "uuid", source = "uuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "businessUuid", source = "businessUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "isPublic", source = "public")
    @Mapping(target = "imageUuids", source = "imageUuIds", qualifiedByName = "fromImageUuIdList")
    @Mapping(target = "archived", source = "lifecycleState.archived")
    @Mapping(target = "softDeleted", source = "lifecycleState.softDeleted")
    @Mapping(target = "createdAt", source = "auditMetadata.createdAt.value")
    @Mapping(target = "lastModifiedAt", source = "auditMetadata.lastModified.value")
    @Mapping(target = "lastModifiedBy", source = "auditMetadata.lastModifiedBy.identity")
    @Mapping(target = "version", source = "optLockVer")
    GalleryEntity toEntity(GalleryAggregate aggregate);

    // --- MAPPING HELPERS ---

    @Named("toGalleryId")
    default GalleryId toGalleryId(Long id) {
        return id != null ? new GalleryId(PkId.of(id)) : null;
    }

    @Named("toGalleryUuId")
    default GalleryUuId toGalleryUuId(java.util.UUID uuid) {
        return uuid != null ? new GalleryUuId(UuId.fromString(uuid.toString())) : null;
    }

    @Named("toBusinessUuId")
    default GalleryBusinessUuId toBusinessUuId(java.util.UUID uuid) {
        return uuid != null ? new GalleryBusinessUuId(UuId.fromString(uuid.toString())) : null;
    }

    @Named("toImageUuIdList")
    default List<ImageUuId> toImageUuIdList(List<java.util.UUID> uuids) {
        if (uuids == null) return Collections.emptyList();
        return uuids.stream()
                .map(u -> new ImageUuId(UuId.fromString(u.toString())))
                .collect(Collectors.toList());
    }

    @Named("fromImageUuIdList")
    default List<java.util.UUID> fromImageUuIdList(List<ImageUuId> imageUuIds) {
        if (imageUuIds == null) return Collections.emptyList();
        return imageUuIds.stream()
                .map(id -> java.util.UUID.fromString(id.value().value()))
                .collect(Collectors.toList());
    }

    @Named("toLifecycleState")
    default LifecycleState toLifecycleState(GalleryEntity entity) {
        return new LifecycleState(entity.isArchived(), entity.isSoftDeleted());
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

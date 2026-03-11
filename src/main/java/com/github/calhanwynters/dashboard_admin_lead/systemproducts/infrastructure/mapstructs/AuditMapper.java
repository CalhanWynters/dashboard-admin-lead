package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.AuditMetadataEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditMapper {

    // Entity -> Domain Record
    @Mapping(target = "createdAt", expression = "java(new CreatedAt(entity.getCreatedAt()))")
    @Mapping(target = "lastModified", expression = "java(new LastModified(entity.getLastModified()))")
    @Mapping(target = "lastModifiedBy", expression = "java(Actor.fromString(entity.getLastModifiedBy()))")
    AuditMetadata toRecord(AuditMetadataEntity entity);

    // Domain Record -> Entity
    @Mapping(target = "createdAt", source = "createdAt.value")
    @Mapping(target = "lastModified", source = "lastModified.value")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy.value")
    AuditMetadataEntity toEntity(AuditMetadata record);
}

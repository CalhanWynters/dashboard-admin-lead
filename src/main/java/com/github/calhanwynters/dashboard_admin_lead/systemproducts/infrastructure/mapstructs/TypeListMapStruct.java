package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.TypeListEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.*;

@Mapper(componentModel = "spring", imports = {Collectors.class, HashSet.class})
public interface TypeListMapStruct {

    // --- RECONSTITUTION: Entity to Aggregate ---
    @Mapping(target = "id", source = "id", qualifiedByName = "toTypeListId")
    @Mapping(target = "uuId", source = "uuid", qualifiedByName = "toTypeListUuId")
    @Mapping(target = "businessUuId", source = "businessUuid", qualifiedByName = "toBusinessUuId")
    @Mapping(target = "typeUuIds", source = "typeUuids", qualifiedByName = "toTypeUuIdSet")
    @Mapping(target = "auditMetadata", source = ".", qualifiedByName = "toAuditMetadata")
    @Mapping(target = "lifecycleState", source = ".", qualifiedByName = "toLifecycleState")
    TypeListAggregate toAggregate(TypeListEntity entity);

    // --- PERSISTENCE: Aggregate to Entity ---
    @Mapping(target = "id", source = "id.value.id")
    @Mapping(target = "uuid", source = "uuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "businessUuid", source = "businessUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "typeUuids", source = "typeUuIds", qualifiedByName = "fromTypeUuIdSet")
    @Mapping(target = "archived", source = "lifecycleState.archived")
    @Mapping(target = "softDeleted", source = "lifecycleState.softDeleted")
    @Mapping(target = "createdAt", source = "auditMetadata.createdAt.value")
    @Mapping(target = "lastModifiedAt", source = "auditMetadata.lastModified.value")
    @Mapping(target = "lastModifiedBy", source = "auditMetadata.lastModifiedBy.identity")
    TypeListEntity toEntity(TypeListAggregate aggregate);

    // --- COLLECTION HELPERS ---

    @Named("toTypeUuIdSet")
    default Set<TypesUuId> toTypeUuIdSet(Set<java.util.UUID> uuids) {
        if (uuids == null) return new HashSet<>();
        return uuids.stream()
                .map(uuid -> new TypesUuId(UuId.fromString(uuid.toString())))
                .collect(Collectors.toSet());
    }

    @Named("fromTypeUuIdSet")
    default Set<java.util.UUID> fromTypeUuIdSet(Set<TypesUuId> typeUuIds) {
        if (typeUuIds == null) return new HashSet<>();
        return typeUuIds.stream()
                .map(id -> java.util.UUID.fromString(id.value().value()))
                .collect(Collectors.toSet());
    }

    // --- STANDARD HELPERS ---

    @Named("toTypeListId")
    default TypeListId toTypeListId(Long id) { return id != null ? new TypeListId(PkId.of(id)) : null; }

    @Named("toTypeListUuId")
    default TypeListUuId toTypeListUuId(java.util.UUID uuid) { return new TypeListUuId(UuId.fromString(uuid.toString())); }

    @Named("toLifecycleState")
    default LifecycleState toLifecycleState(TypeListEntity entity) {
        return new LifecycleState(entity.isArchived(), entity.isSoftDeleted());
    }

    @Named("stringToUuid")
    default java.util.UUID stringToUuid(String value) { return value != null ? java.util.UUID.fromString(value) : null; }
}

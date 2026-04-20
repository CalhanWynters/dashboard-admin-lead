package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.VariantListEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.*;

@Mapper(componentModel = "spring", imports = {Collectors.class, HashSet.class})
public interface VariantListMapStruct {

    // --- RECONSTITUTION: Entity to Aggregate ---
    @Mapping(target = "id", source = "id", qualifiedByName = "toVariantListId")
    @Mapping(target = "uuId", source = "uuid", qualifiedByName = "toVariantListUuId")
    @Mapping(target = "businessUuId", source = "businessUuid", qualifiedByName = "toBusinessUuId")
    @Mapping(target = "variantUuIds", source = "variantUuids", qualifiedByName = "toVariantUuIdSet")
    @Mapping(target = "auditMetadata", source = ".", qualifiedByName = "toAuditMetadata")
    @Mapping(target = "lifecycleState", source = ".", qualifiedByName = "toLifecycleState")
    VariantListAggregate toAggregate(VariantListEntity entity);

    // --- PERSISTENCE: Aggregate to Entity ---
    @Mapping(target = "id", source = "id.value.id")
    @Mapping(target = "uuid", source = "uuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "businessUuid", source = "businessUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "variantUuids", source = "variantUuIds", qualifiedByName = "fromVariantUuIdSet")
    @Mapping(target = "archived", source = "lifecycleState.archived")
    @Mapping(target = "softDeleted", source = "lifecycleState.softDeleted")
    @Mapping(target = "createdAt", source = "auditMetadata.createdAt.value")
    @Mapping(target = "lastModifiedAt", source = "auditMetadata.lastModified.value")
    @Mapping(target = "lastModifiedBy", source = "auditMetadata.lastModifiedBy.identity")
    VariantListEntity toEntity(VariantListAggregate aggregate);

    // --- COLLECTION HELPERS ---

    @Named("toVariantUuIdSet")
    default Set<VariantsUuId> toVariantUuIdSet(Set<java.util.UUID> uuids) {
        if (uuids == null) return new HashSet<>();
        return uuids.stream()
                .map(uuid -> new VariantsUuId(UuId.fromString(uuid.toString())))
                .collect(Collectors.toSet());
    }

    @Named("fromVariantUuIdSet")
    default Set<java.util.UUID> fromVariantUuIdSet(Set<VariantsUuId> variantUuIds) {
        if (variantUuIds == null) return new HashSet<>();
        return variantUuIds.stream()
                .map(id -> java.util.UUID.fromString(id.value().value()))
                .collect(Collectors.toSet());
    }

    // --- STANDARD HELPERS ---

    @Named("toVariantListId")
    default VariantListId toVariantListId(Long id) {
        return id != null ? new VariantListId(PkId.of(id)) : null;
    }

    @Named("toVariantListUuId")
    default VariantListUuId toVariantListUuId(java.util.UUID uuid) {
        return new VariantListUuId(UuId.fromString(uuid.toString()));
    }

    @Named("toLifecycleState")
    default LifecycleState toLifecycleState(VariantListEntity entity) {
        return new LifecycleState(entity.isArchived(), entity.isSoftDeleted());
    }

    @Named("stringToUuid")
    default java.util.UUID stringToUuid(String value) {
        return value != null ? java.util.UUID.fromString(value) : null;
    }
}
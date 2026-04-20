package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.VariantsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;

@Mapper(componentModel = "spring", imports = {Collectors.class, HashSet.class})
public interface VariantsMapStruct {

    // --- RECONSTITUTION: Entity to Aggregate ---
    @Mapping(target = "id", source = "id", qualifiedByName = "toVariantsId")
    @Mapping(target = "uuId", source = "uuid", qualifiedByName = "toVariantsUuId")
    @Mapping(target = "businessUuId", source = "businessUuid", qualifiedByName = "toBusinessUuId")
    @Mapping(target = "variantsName", source = "name", qualifiedByName = "toVariantsName")
    @Mapping(target = "variantsRegion", source = "region", qualifiedByName = "toVariantsRegion")
    @Mapping(target = "featureUuIds", source = "featureUuids", qualifiedByName = "toFeatureUuIdSet")
    @Mapping(target = "auditMetadata", source = ".", qualifiedByName = "toAuditMetadata")
    @Mapping(target = "lifecycleState", source = ".", qualifiedByName = "toLifecycleState")
    VariantsAggregate toAggregate(VariantsEntity entity);

    // --- PERSISTENCE: Aggregate to Entity ---
    @Mapping(target = "id", source = "id.value.id")
    @Mapping(target = "uuid", source = "uuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "businessUuid", source = "businessUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "name", source = "variantsName.value.name")
    @Mapping(target = "region", source = "variantsRegion.value.value")
    @Mapping(target = "featureUuids", source = "assignedFeatureUuIds", qualifiedByName = "fromFeatureUuIdSet")
    @Mapping(target = "archived", source = "lifecycleState.archived")
    @Mapping(target = "softDeleted", source = "lifecycleState.softDeleted")
    @Mapping(target = "createdAt", source = "auditMetadata.createdAt.value")
    @Mapping(target = "lastModifiedAt", source = "auditMetadata.lastModified.value")
    @Mapping(target = "lastModifiedBy", source = "auditMetadata.lastModifiedBy.identity")
    VariantsEntity toEntity(VariantsAggregate aggregate);

    // --- VARIANTS SPECIFIC HELPERS ---

    @Named("toVariantsId")
    default VariantsId toVariantsId(Long id) {
        return id != null ? new VariantsId(PkId.of(id)) : null;
    }

    @Named("toVariantsUuId")
    default VariantsUuId toVariantsUuId(java.util.UUID uuid) {
        return new VariantsUuId(UuId.fromString(uuid.toString()));
    }

    @Named("toVariantsName")
    default VariantsName toVariantsName(String name) {
        return new VariantsName(new Name(name));
    }

    @Named("toVariantsRegion")
    default VariantsRegion toVariantsRegion(String r) { return (r == null) ? VariantsRegion.from(Region.GLOBAL) : VariantsRegion.from(Region.from(r)); }

    @Named("toFeatureUuIdSet")
    default Set<FeatureUuId> toFeatureUuIdSet(Set<java.util.UUID> uuids) {
        if (uuids == null) return new HashSet<>();
        return uuids.stream()
                .map(uuid -> new FeatureUuId(UuId.fromString(uuid.toString())))
                .collect(Collectors.toSet());
    }

    @Named("fromFeatureUuIdSet")
    default Set<java.util.UUID> fromFeatureUuIdSet(Set<FeatureUuId> featureUuIds) {
        if (featureUuIds == null) return new HashSet<>();
        return featureUuIds.stream()
                .map(id -> java.util.UUID.fromString(id.value().value()))
                .collect(Collectors.toSet());
    }

    @Named("toLifecycleState")
    default LifecycleState toLifecycleState(VariantsEntity entity) {
        return new LifecycleState(entity.isArchived(), entity.isSoftDeleted());
    }

    @Named("stringToUuid")
    default java.util.UUID stringToUuid(String value) {
        return value != null ? java.util.UUID.fromString(value) : null;
    }
}
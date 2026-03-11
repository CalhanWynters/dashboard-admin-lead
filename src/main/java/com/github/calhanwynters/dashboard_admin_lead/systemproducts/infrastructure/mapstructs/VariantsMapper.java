package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleansLEGACY;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.VariantsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;

@Mapper(componentModel = "spring")
public interface VariantsMapper {

    @Mapping(target = "variantsId", source = "id", qualifiedByName = "toVariantsId")
    @Mapping(target = "variantsUuId", source = "uuid", qualifiedByName = "toVariantsUuId")
    @Mapping(target = "variantsBusinessUuId", source = "businessUuid", qualifiedByName = "toBusinessUuId")
    @Mapping(target = "variantsName", source = "name", qualifiedByName = "toVariantsName")
    @Mapping(target = "assignedFeatureUuIds", source = "assignedFeatureUuids", qualifiedByName = "toFeatureUuIdSet")
    @Mapping(target = "productBooleans", source = ".", qualifiedByName = "toProductBooleans")
    @Mapping(target = "auditMetadata", source = ".", qualifiedByName = "toAuditMetadata")
    VariantsAggregateLEGACY toAggregate(VariantsEntity entity);

    @Mapping(target = "id", source = "variantsId.value.id")
    @Mapping(target = "uuid", source = "variantsUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "businessUuid", source = "variantsBusinessUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "name", source = "variantsName.value.name")
    @Mapping(target = "assignedFeatureUuids", source = "assignedFeatureUuIds", qualifiedByName = "fromFeatureUuIdSet")
    @Mapping(target = "archived", source = "productBooleans.archived")
    @Mapping(target = "softDeleted", source = "productBooleans.softDeleted")
    @Mapping(target = "createdAt", source = "auditMetadata.createdAt.value")
    @Mapping(target = "lastModifiedAt", source = "auditMetadata.lastModified.value")
    @Mapping(target = "lastModifiedBy", source = "auditMetadata.lastModifiedBy.identity")
    VariantsEntity toEntity(VariantsAggregateLEGACY aggregate);

    // --- Collection Helpers ---

    @Named("toFeatureUuIdSet")
    default Set<FeatureUuId> toFeatureUuIdSet(Set<UUID> uuids) {
        if (uuids == null) return Collections.emptySet();
        return uuids.stream()
                .map(u -> new FeatureUuId(new UuId(u.toString())))
                .collect(Collectors.toSet());
    }

    @Named("fromFeatureUuIdSet")
    default Set<UUID> fromFeatureUuIdSet(Set<FeatureUuId> featureUuIds) {
        if (featureUuIds == null) return Collections.emptySet();
        return featureUuIds.stream()
                .map(f -> UUID.fromString(f.value().value()))
                .collect(Collectors.toSet());
    }

    // --- Standard Helpers ---

    @Named("toVariantsId")
    default VariantsId toVariantsId(Long id) { return id != null ? new VariantsId(PkId.of(id)) : null; }

    @Named("toVariantsUuId")
    default VariantsUuId toVariantsUuId(UUID uuid) { return new VariantsUuId(UuId.fromString(uuid.toString())); }

    @Named("toBusinessUuId")
    default VariantsBusinessUuId toBusinessUuId(UUID uuid) { return new VariantsBusinessUuId(UuId.fromString(uuid.toString())); }

    @Named("toVariantsName")
    default VariantsName toVariantsName(String name) { return new VariantsName(new Name(name)); }

    @Named("toProductBooleans")
    default ProductBooleansLEGACY toProductBooleans(VariantsEntity entity) {
        return new ProductBooleansLEGACY(entity.isArchived(), entity.isSoftDeleted());
    }

    @Named("toAuditMetadata")
    default AuditMetadata toAuditMetadata(VariantsEntity entity) {
        return AuditMetadata.reconstitute(
                new CreatedAt(entity.getCreatedAt()),
                new LastModified(entity.getLastModifiedAt()),
                new Actor(entity.getLastModifiedBy(), Collections.emptySet())
        );
    }

    @Named("stringToUuid")
    default java.util.UUID stringToUuid(String value) { return value != null ? java.util.UUID.fromString(value) : null; }
}

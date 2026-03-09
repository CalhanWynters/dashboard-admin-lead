package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.VariantListEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.*;

@Mapper(componentModel = "spring")
public interface VariantListMapper {

    @Mapping(target = "variantListId", source = "id", qualifiedByName = "toVariantListId")
    @Mapping(target = "variantListUuId", source = "uuid", qualifiedByName = "toVariantListUuId")
    @Mapping(target = "variantListBusinessUuId", source = "businessUuid", qualifiedByName = "toBusinessUuId")
    @Mapping(target = "variantUuIds", source = "variantUuids", qualifiedByName = "toVariantUuIdSet")
    @Mapping(target = "productBooleans", source = ".", qualifiedByName = "toProductBooleans")
    @Mapping(target = "auditMetadata", source = ".", qualifiedByName = "toAuditMetadata")
    VariantListAggregate toAggregate(VariantListEntity entity);

    @Mapping(target = "id", source = "variantListId.value.id")
    @Mapping(target = "uuid", source = "variantListUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "businessUuid", source = "variantListBusinessUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "variantUuids", source = "variantUuIds", qualifiedByName = "fromVariantUuIdSet")
    @Mapping(target = "archived", source = "productBooleans.archived")
    @Mapping(target = "softDeleted", source = "productBooleans.softDeleted")
    @Mapping(target = "createdAt", source = "auditMetadata.createdAt.value")
    @Mapping(target = "lastModifiedAt", source = "auditMetadata.lastModified.value")
    @Mapping(target = "lastModifiedBy", source = "auditMetadata.lastModifiedBy.identity")
    VariantListEntity toEntity(VariantListAggregate aggregate);

    // --- Set Converters ---

    @Named("toVariantUuIdSet")
    default Set<VariantsUuId> toVariantUuIdSet(Set<UUID> uuids) {
        if (uuids == null) return Collections.emptySet();
        return uuids.stream()
                .map(u -> new VariantsUuId(new UuId(u.toString())))
                .collect(Collectors.toSet());
    }

    @Named("fromVariantUuIdSet")
    default Set<UUID> fromVariantUuIdSet(Set<VariantsUuId> variantUuIds) {
        if (variantUuIds == null) return Collections.emptySet();
        return variantUuIds.stream()
                .map(v -> UUID.fromString(v.value().value()))
                .collect(Collectors.toSet());
    }

    // --- Value Object Helpers ---

    @Named("toVariantListId")
    default VariantListId toVariantListId(Long id) { return id != null ? new VariantListId(PkId.of(id)) : null; }

    @Named("toVariantListUuId")
    default VariantListUuId toVariantListUuId(UUID uuid) { return new VariantListUuId(UuId.fromString(uuid.toString())); }

    @Named("toBusinessUuId")
    default VariantListBusinessUuId toBusinessUuId(UUID uuid) { return new VariantListBusinessUuId(UuId.fromString(uuid.toString())); }

    @Named("toProductBooleans")
    default ProductBooleans toProductBooleans(VariantListEntity entity) {
        return new ProductBooleans(entity.isArchived(), entity.isSoftDeleted());
    }

    @Named("toAuditMetadata")
    default AuditMetadata toAuditMetadata(VariantListEntity entity) {
        return AuditMetadata.reconstitute(
                new CreatedAt(entity.getCreatedAt()),
                new LastModified(entity.getLastModifiedAt()),
                new Actor(entity.getLastModifiedBy(), Collections.emptySet())
        );
    }

    @Named("stringToUuid")
    default java.util.UUID stringToUuid(String value) { return value != null ? java.util.UUID.fromString(value) : null; }
}

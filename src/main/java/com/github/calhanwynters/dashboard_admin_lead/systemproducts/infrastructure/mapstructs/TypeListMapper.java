package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleansLEGACY;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.TypeListEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.*;

@Mapper(componentModel = "spring")
public interface TypeListMapper {

    @Mapping(target = "typeListId", source = "id", qualifiedByName = "toTypeListId")
    @Mapping(target = "typeListUuId", source = "uuid", qualifiedByName = "toTypeListUuId")
    @Mapping(target = "typeListBusinessUuId", source = "businessUuid", qualifiedByName = "toBusinessUuId")
    @Mapping(target = "typeUuIds", source = "typeUuIds", qualifiedByName = "toTypesUuIdSet")
    @Mapping(target = "productBooleans", source = ".", qualifiedByName = "toProductBooleans")
    @Mapping(target = "auditMetadata", source = ".", qualifiedByName = "toAuditMetadata")
    TypeListAggregateLEGACY toAggregate(TypeListEntity entity);

    @Mapping(target = "id", source = "typeListId.value.id")
    @Mapping(target = "uuid", source = "typeListUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "businessUuid", source = "typeListBusinessUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "typeUuIds", source = "typeUuIds", qualifiedByName = "fromTypesUuIdSet")
    @Mapping(target = "archived", source = "productBooleans.archived")
    @Mapping(target = "softDeleted", source = "productBooleans.softDeleted")
    @Mapping(target = "createdAt", source = "auditMetadata.createdAt.value")
    @Mapping(target = "lastModifiedAt", source = "auditMetadata.lastModified.value")
    @Mapping(target = "lastModifiedBy", source = "auditMetadata.lastModifiedBy.identity")
    TypeListEntity toEntity(TypeListAggregateLEGACY aggregate);

    // --- Collection Helpers ---

    @Named("toTypesUuIdSet")
    default Set<TypesUuId> toTypesUuIdSet(Set<UUID> uuids) {
        if (uuids == null) return Collections.emptySet();
        return uuids.stream()
                .map(u -> new TypesUuId(new UuId(u.toString())))
                .collect(Collectors.toSet());
    }

    @Named("fromTypesUuIdSet")
    default Set<UUID> fromTypesUuIdSet(Set<TypesUuId> typesUuIds) {
        if (typesUuIds == null) return Collections.emptySet();
        return typesUuIds.stream()
                .map(t -> UUID.fromString(t.value().value()))
                .collect(Collectors.toSet());
    }

    // --- Standard Helpers ---

    @Named("toTypeListId")
    default TypeListId toTypeListId(Long id) { return id != null ? new TypeListId(PkId.of(id)) : null; }

    @Named("toTypeListUuId")
    default TypeListUuId toTypeListUuId(UUID uuid) { return new TypeListUuId(UuId.fromString(uuid.toString())); }

    @Named("toBusinessUuId")
    default TypeListBusinessUuId toBusinessUuId(UUID uuid) { return new TypeListBusinessUuId(UuId.fromString(uuid.toString())); }

    @Named("toProductBooleans")
    default ProductBooleansLEGACY toProductBooleans(TypeListEntity entity) {
        return new ProductBooleansLEGACY(entity.isArchived(), entity.isSoftDeleted());
    }

    @Named("toAuditMetadata")
    default AuditMetadata toAuditMetadata(TypeListEntity entity) {
        return AuditMetadata.reconstitute(
                new CreatedAt(entity.getCreatedAt()),
                new LastModified(entity.getLastModifiedAt()),
                new Actor(entity.getLastModifiedBy(), Collections.emptySet())
        );
    }

    @Named("stringToUuid")
    default java.util.UUID stringToUuid(String value) { return value != null ? java.util.UUID.fromString(value) : null; }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.TypesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;

@Mapper(componentModel = "spring")
public interface TypesMapper {

    @Mapping(target = "typesId", source = "id", qualifiedByName = "toTypesId")
    @Mapping(target = "typesUuId", source = "uuid", qualifiedByName = "toTypesUuId")
    @Mapping(target = "typesBusinessUuId", source = "businessUuid", qualifiedByName = "toBusinessUuId")
    @Mapping(target = "typesName", source = "name", qualifiedByName = "toTypesName")
    @Mapping(target = "typesPhysicalSpecs", source = ".", qualifiedByName = "toTypesPhysicalSpecs")
    @Mapping(target = "productBooleans", source = ".", qualifiedByName = "toBooleans")
    @Mapping(target = "auditMetadata", source = ".", qualifiedByName = "toAuditMetadata")
    TypesAggregateLEGACY toAggregate(TypesEntity entity);

    @Mapping(target = "id", source = "typesId.value.id")
    @Mapping(target = "uuid", source = "typesUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "businessUuid", source = "typesBusinessUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "name", source = "typesName.value.name")
    @Mapping(target = "weightAmount", source = "typesPhysicalSpecs.value.weight.amount")
    @Mapping(target = "weightUnit", source = "typesPhysicalSpecs.value.weight.weightUnit.name")
    @Mapping(target = "length", source = "typesPhysicalSpecs.value.dimensions.length")
    @Mapping(target = "width", source = "typesPhysicalSpecs.value.dimensions.width")
    @Mapping(target = "height", source = "typesPhysicalSpecs.value.dimensions.height")
    @Mapping(target = "dimensionUnit", source = "typesPhysicalSpecs.value.dimensions.sizeUnit.code")
    @Mapping(target = "careInstructions", source = "typesPhysicalSpecs.value.careInstructions.value.value")
    @Mapping(target = "archived", source = "productBooleans.archived")
    @Mapping(target = "softDeleted", source = "productBooleans.softDeleted")
    @Mapping(target = "createdAt", source = "auditMetadata.createdAt.value")
    @Mapping(target = "lastModifiedAt", source = "auditMetadata.lastModified.value")
    @Mapping(target = "lastModifiedBy", source = "auditMetadata.lastModifiedBy.identity")
    TypesEntity toEntity(TypesAggregateLEGACY aggregate);

    // --- Complex Composite Helper ---

    @Named("toTypesPhysicalSpecs")
    default TypesPhysicalSpecs toTypesPhysicalSpecs(TypesEntity entity) {
        if (entity.getWeightAmount() == null && entity.getLength() == null) {
            return TypesPhysicalSpecs.NONE;
        }
        return new TypesPhysicalSpecs(new PhysicalSpecs(
                new Weight(entity.getWeightAmount(), WeightUnitEnums.fromString(entity.getWeightUnit())),
                new Dimensions(entity.getLength(), entity.getWidth(), entity.getHeight(), DimensionUnitEnums.fromCode(entity.getDimensionUnit())),
                new CareInstruction(entity.getCareInstructions())
        ));
    }

    // --- Standard Helpers ---

    @Named("toTypesId")
    default TypesId toTypesId(Long id) { return id != null ? new TypesId(PkId.of(id)) : null; }

    @Named("toTypesUuId")
    default TypesUuId toTypesUuId(java.util.UUID uuid) { return new TypesUuId(UuId.fromString(uuid.toString())); }

    @Named("toBusinessUuId")
    default TypesBusinessUuId toBusinessUuId(java.util.UUID uuid) { return new TypesBusinessUuId(UuId.fromString(uuid.toString())); }

    @Named("toTypesName")
    default TypesName toTypesName(String name) { return new TypesName(new Name(name)); }

    @Named("toBooleans")
    default ProductBooleans toBooleans(TypesEntity entity) { return new ProductBooleans(entity.isArchived(), entity.isSoftDeleted()); }

    @Named("toAuditMetadata")
    default AuditMetadata toAuditMetadata(TypesEntity entity) {
        return AuditMetadata.reconstitute(
                new CreatedAt(entity.getCreatedAt()),
                new LastModified(entity.getLastModifiedAt()),
                new Actor(entity.getLastModifiedBy(), Collections.emptySet())
        );
    }

    @Named("stringToUuid")
    default java.util.UUID stringToUuid(String value) { return value != null ? java.util.UUID.fromString(value) : null; }
}

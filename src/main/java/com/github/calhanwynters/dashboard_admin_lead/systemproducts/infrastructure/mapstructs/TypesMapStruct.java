package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.TypesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;

@Mapper(componentModel = "spring")
public interface TypesMapStruct {

    // --- RECONSTITUTION: Entity to Aggregate ---
    @Mapping(target = "id", source = "id", qualifiedByName = "toTypesId")
    @Mapping(target = "uuId", source = "uuid", qualifiedByName = "toTypesUuId")
    @Mapping(target = "businessUuId", source = "businessUuid", qualifiedByName = "toBusinessUuId")
    @Mapping(target = "typesName", source = "name", qualifiedByName = "toTypesName")
    @Mapping(target = "typesRegion", source = "region", qualifiedByName = "toTypesRegion")
    @Mapping(target = "typesPhysicalSpecs", source = ".", qualifiedByName = "toTypesPhysicalSpecs")
    @Mapping(target = "auditMetadata", source = ".", qualifiedByName = "toAuditMetadata")
    @Mapping(target = "lifecycleState", source = ".", qualifiedByName = "toLifecycleState")
    TypesAggregate toAggregate(TypesEntity entity);

    // --- PERSISTENCE: Aggregate to Entity ---
    @Mapping(target = "id", source = "id.value.id")
    @Mapping(target = "uuid", source = "uuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "businessUuid", source = "businessUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "name", source = "typesName.value.name")
    @Mapping(target = "region", source = "typesRegion.value.value")
    @Mapping(target = "weightAmount", source = "typesPhysicalSpecs.value.weight.amount")
    @Mapping(target = "weightUnit", source = "typesPhysicalSpecs.value.weight.weightUnit.name")
    @Mapping(target = "length", source = "typesPhysicalSpecs.value.dimensions.length")
    @Mapping(target = "width", source = "typesPhysicalSpecs.value.dimensions.width")
    @Mapping(target = "height", source = "typesPhysicalSpecs.value.dimensions.height")
    @Mapping(target = "dimensionUnit", source = "typesPhysicalSpecs.value.dimensions.sizeUnit.code")
    @Mapping(target = "careInstructions", source = "typesPhysicalSpecs.value.careInstructions.value.value")
    @Mapping(target = "archived", source = "lifecycleState.archived")
    @Mapping(target = "softDeleted", source = "lifecycleState.softDeleted")
    @Mapping(target = "createdAt", source = "auditMetadata.createdAt.value")
    @Mapping(target = "lastModifiedAt", source = "auditMetadata.lastModified.value")
    @Mapping(target = "lastModifiedBy", source = "auditMetadata.lastModifiedBy.identity")
    TypesEntity toEntity(TypesAggregate aggregate);

    // --- TYPES SPECIFIC HELPERS ---

    @Named("toTypesId")
    default TypesId toTypesId(Long id) { return id != null ? new TypesId(PkId.of(id)) : null; }

    @Named("toTypesUuId")
    default TypesUuId toTypesUuId(java.util.UUID uuid) { return new TypesUuId(UuId.fromString(uuid.toString())); }

    @Named("toTypesName")
    default TypesName toTypesName(String name) { return new TypesName(new Name(name)); }

    @Named("toTypesRegion")
    default TypesRegion toTypesRegion(String r) { return (r == null) ? TypesRegion.from(Region.GLOBAL) : TypesRegion.from(Region.from(r)); }


    @Named("toTypesPhysicalSpecs")
    default TypesPhysicalSpecs toTypesPhysicalSpecs(TypesEntity entity) {
        if (entity.getWeightAmount() == null && entity.getLength() == null) {
            return TypesPhysicalSpecs.NONE;
        }
        // Logic identical to ProductPhysicalSpecs, but wrapped for the Types domain
        Weight weight = new Weight(entity.getWeightAmount(), WeightUnitEnums.fromString(entity.getWeightUnit()));
        Dimensions dimensions = new Dimensions(entity.getLength(), entity.getWidth(), entity.getHeight(), DimensionUnitEnums.fromCode(entity.getDimensionUnit()));
        CareInstruction care = new CareInstruction(entity.getCareInstructions());

        return new TypesPhysicalSpecs(new PhysicalSpecs(weight, dimensions, care));
    }

    @Named("toLifecycleState")
    default LifecycleState toLifecycleState(TypesEntity entity) {
        return new LifecycleState(entity.isArchived(), entity.isSoftDeleted());
    }

    @Named("stringToUuid")
    default java.util.UUID stringToUuid(String value) { return value != null ? java.util.UUID.fromString(value) : null; }
}
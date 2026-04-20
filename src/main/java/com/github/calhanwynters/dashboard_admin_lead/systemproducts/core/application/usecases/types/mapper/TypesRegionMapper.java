package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Region;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypesRegionDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct Mapper for Types Region transitions.
 * Supports Spring component model for easy injection into Use Cases.
 */
@Mapper(componentModel = "spring")
public interface TypesRegionMapper {

    /**
     * Maps the incoming DTO directly to the hardened Domain Value Object.
     */
    @Mapping(target = ".", source = "newRegionValue", qualifiedByName = "toHardenedTypesRegion")
    TypesRegion toDomain(TypesRegionDTO dto);

    /**
     * Hardened Converter: Ensures raw string input is validated by the Region record.
     */
    @Named("toHardenedTypesRegion")
    default TypesRegion toHardenedTypesRegion(String value) {
        // Triggers DomainGuard checks in Region.from()
        return TypesRegion.from(Region.from(value != null ? value : "GLOBAL"));
    }
}
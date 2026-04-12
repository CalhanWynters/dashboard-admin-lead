package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Region;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsRegionDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsRegion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct Mapper for Variants Region transitions.
 * Supports Spring component model for easy injection into Use Cases.
 */
@Mapper(componentModel = "spring")
public interface VariantsRegionMapper {

    /**
     * Maps the incoming DTO directly to the hardened Domain Value Object.
     */
    @Mapping(target = ".", source = "newRegionValue", qualifiedByName = "toHardenedVariantsRegion")
    VariantsRegion toDomain(VariantsRegionDTO dto);

    /**
     * Hardened Converter: Ensures raw string input is validated by the Region record.
     */
    @Named("toHardenedVariantsRegion")
    default VariantsRegion toHardenedVariantsRegion(String value) {
        // Triggers DomainGuard checks in Region.from()
        return VariantsRegion.from(Region.from(value != null ? value : "GLOBAL"));
    }
}

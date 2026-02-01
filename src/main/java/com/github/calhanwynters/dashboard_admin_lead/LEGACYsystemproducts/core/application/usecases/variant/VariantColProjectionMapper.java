package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.application.usecases.variant;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.variant.VariantCollectionAggregate;

public class VariantColProjectionMapper {
    public static VariantColProjectionDTO toDTO(VariantCollectionAggregate aggregate) {
        VariantColProjectionDTO dto = new VariantColProjectionDTO();
        dto.primaryKey = aggregate.getPrimaryKey();
        dto.variantColId = aggregate.getVariantColId().value(); // assuming .value() returns String
        dto.businessId = aggregate.getBusinessId().value();

        // Convert Set<UuId> to List<String> for the DTO
        dto.variantIds = aggregate.getVariantIds().stream()
                .map(UuId::value)
                .toList();

        return dto;
    }
}

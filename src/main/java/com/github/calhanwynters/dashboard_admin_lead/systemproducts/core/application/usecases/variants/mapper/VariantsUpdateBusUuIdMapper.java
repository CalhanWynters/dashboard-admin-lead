package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsUpdateBusUuIdDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsAggregate;

public final class VariantsUpdateBusUuIdMapper {
    private VariantsUpdateBusUuIdMapper() {}

    public static void mapToUpdate(VariantsUpdateBusUuIdDTO dto, VariantsAggregate aggregate) {
        if (dto == null || aggregate == null) return;
        aggregate.updateBusinessUuId(dto.toVariantsBusinessUuId(), dto.toActor());
    }
}

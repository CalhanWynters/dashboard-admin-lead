package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsSoftDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsAggregate;

public final class VariantsSoftMapper {
    private VariantsSoftMapper() {}

    public static void mapToSoftDelete(VariantsSoftDTO dto, VariantsAggregate aggregate) {
        if (dto == null || aggregate == null) return;
        aggregate.softDelete(dto.toActor());
    }
}

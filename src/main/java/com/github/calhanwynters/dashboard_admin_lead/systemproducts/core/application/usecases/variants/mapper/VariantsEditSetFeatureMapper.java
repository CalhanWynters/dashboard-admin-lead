package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsEditSetFeatureDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsAggregate;

public final class VariantsEditSetFeatureMapper {
    private VariantsEditSetFeatureMapper() {}

    public static void mapToAssignment(VariantsEditSetFeatureDTO dto, VariantsAggregate aggregate) {
        if (dto == null || aggregate == null) return;
        aggregate.assignFeature(dto.toFeatureUuId(), dto.toActor());
    }

    public static void mapToUnassignment(VariantsEditSetFeatureDTO dto, VariantsAggregate aggregate) {
        if (dto == null || aggregate == null) return;
        aggregate.unassignFeature(dto.toFeatureUuId(), dto.toActor());
    }
}

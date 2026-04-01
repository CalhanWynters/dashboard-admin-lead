package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto.FeatureUpdateBusUuIdDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureBusinessUuId;
import java.util.Set;

/**
 * Mapper for Business UUID update operations.
 * Bridges the gap between External IDs and Domain Value Objects.
 */
public final class FeatureUpdateBusUuIdMapper {

    private FeatureUpdateBusUuIdMapper() {} // Static utility only

    /**
     * Map the Update DTO to a Domain FeatureBusinessUuId.
     * Triggers DomainGuard validation immediately.
     */
    public static FeatureBusinessUuId toDomainBusinessUuId(FeatureUpdateBusUuIdDTO dto) {
        return new FeatureBusinessUuId(UuId.fromString(dto.newBusinessUuid()));
    }

    /**
     * Map the Update DTO to a Domain Actor.
     */
    public static Actor toActor(FeatureUpdateBusUuIdDTO dto) {
        return Actor.of(
                dto.actorId(),
                dto.actorRoles() != null ? dto.actorRoles() : Set.of()
        );
    }
}

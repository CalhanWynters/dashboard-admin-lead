package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.dto.ImagesUpdateBusUuIdDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImagesBusinessUuId;

import java.util.Set;

/**
 * Mapper for Image Business UUID update operations.
 * Bridges raw external identifiers to Domain Value Objects.
 */
public final class ImagesUpdateBusUuIdMapper {

    private ImagesUpdateBusUuIdMapper() {} // Static utility only

    /**
     * Map the Update DTO to a Domain ImagesBusinessUuId.
     * Triggers DomainGuard validation for RFC 9562 compliance.
     */
    public static ImagesBusinessUuId toDomainBusinessUuId(ImagesUpdateBusUuIdDTO dto) {
        return new ImagesBusinessUuId(UuId.fromString(dto.newBusinessUuid()));
    }

    /**
     * Map the Update DTO to a Domain Actor.
     */
    public static Actor toActor(ImagesUpdateBusUuIdDTO dto) {
        return Actor.of(
                dto.actorId(),
                dto.actorRoles() != null ? dto.actorRoles() : Set.of()
        );
    }
}

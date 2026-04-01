package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.dto.ImagesUpdateURLDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUrl;

import java.util.Set;

/**
 * Mapper for Image URL update operations.
 * Bridges raw URL strings to the Domain ImageUrl record.
 */
public final class ImagesUpdateURLMapper {

    private ImagesUpdateURLMapper() {} // Static utility only

    /**
     * Map the Update DTO to a Domain ImageUrl.
     * Ready for future DomainGuard URL validation logic.
     */
    public static ImageUrl toDomainUrl(ImagesUpdateURLDTO dto) {
        return ImageUrl.of(dto.newUrl());
    }

    /**
     * Map the Update DTO to a Domain Actor.
     */
    public static Actor toActor(ImagesUpdateURLDTO dto) {
        return Actor.of(
                dto.actorId(),
                dto.actorRoles() != null ? dto.actorRoles() : Set.of()
        );
    }
}

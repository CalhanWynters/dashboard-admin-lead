package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.Description;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.dto.ImagesUpdateDescriptionDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageDescription;

import java.util.Set;

/**
 * Mapper for Image Description update operations.
 * Bridges raw text input to the hardened Domain ImageDescription.
 */
public final class ImagesUpdateDescriptionMapper {

    private ImagesUpdateDescriptionMapper() {} // Static utility only

    /**
     * Map the Update DTO to a Domain ImageDescription.
     * Triggers DomainGuard validation for length and lexical content.
     */
    public static ImageDescription toDomainDescription(ImagesUpdateDescriptionDTO dto) {
        return new ImageDescription(Description.from(dto.newDescription()));
    }

    /**
     * Map the Update DTO to a Domain Actor.
     */
    public static Actor toActor(ImagesUpdateDescriptionDTO dto) {
        return Actor.of(
                dto.actorId(),
                dto.actorRoles() != null ? dto.actorRoles() : Set.of()
        );
    }
}

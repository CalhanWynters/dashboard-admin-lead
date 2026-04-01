package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.dto.ImagesRenameDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageName;
import java.util.Set;

/**
 * Mapper for Image Rename operations.
 * Bridges API Request DTOs to Domain Value Objects.
 */
public final class ImagesRenameMapper {

    private ImagesRenameMapper() {} // Static utility only

    /**
     * Map the Rename DTO to a Domain ImageName.
     * Triggers DomainGuard validation immediately.
     */
    public static ImageName toDomainName(ImagesRenameDTO dto) {
        return new ImageName(Name.from(dto.newName()));
    }

    /**
     * Map the Rename DTO to a Domain Actor.
     */
    public static Actor toActor(ImagesRenameDTO dto) {
        return Actor.of(
                dto.actorId(),
                dto.actorRoles() != null ? dto.actorRoles() : Set.of()
        );
    }
}

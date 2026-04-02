package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.dto.GalleryTogglePublicStatusDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;

/**
 * Orchestrates the conversion of Gallery visibility toggle requests.
 * Bridges the REST path variable and the toggle status/actor payload.
 */
public final class GalleryTogglePublicStatusMapper {

    private GalleryTogglePublicStatusMapper() { } // Static utility only

    /**
     * Maps the raw Gallery path ID and DTO payload into a validated Command.
     * Triggers DomainGuard if the galleryUuid is syntactically invalid.
     */
    public static GalleryTogglePublicStatusCommand toCommand(String galleryUuid, GalleryTogglePublicStatusDTO dto) {
        DomainGuard.notBlank(galleryUuid, "Gallery UUID Path Variable");

        return new GalleryTogglePublicStatusCommand(
                new GalleryUuId(UuId.fromString(galleryUuid)),
                dto.newPublicStatus(),
                dto.toActor()
        );
    }

    /**
     * Internal Command record for the Use Case.
     * Pairs the target aggregate identity with the new visibility state and actor context.
     */
    public record GalleryTogglePublicStatusCommand(
            GalleryUuId galleryUuId,
            boolean newPublicStatus,
            Actor actor
    ) {}
}

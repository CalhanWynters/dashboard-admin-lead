package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.dto.GallerySoftDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;

/**
 * Orchestrates the conversion of Gallery soft-deletion requests.
 * Bridges the REST path variable and the actor authorization payload.
 */
public final class GallerySoftMapper {

    private GallerySoftMapper() { } // Static utility only

    /**
     * Maps the raw Gallery path ID and DTO payload into a validated Command.
     * Triggers DomainGuard if the galleryUuid is syntactically invalid.
     */
    public static GallerySoftCommand toCommand(String galleryUuid, GallerySoftDTO dto) {
        DomainGuard.notBlank(galleryUuid, "Gallery UUID Path Variable");

        return new GallerySoftCommand(
                new GalleryUuId(UuId.fromString(galleryUuid)),
                dto.toActor()
        );
    }

    /**
     * Internal Command record for the Use Case.
     * Pairs the target aggregate identity with the actor requesting the deletion.
     */
    public record GallerySoftCommand(
            GalleryUuId galleryUuId,
            Actor actor
    ) {}
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.dto.GalleryArchiveDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;

/**
 * Orchestrates the translation of raw request data into
 * hardened Domain types for Gallery Archival.
 */
public final class GalleryArchiveMapper {

    private GalleryArchiveMapper() { } // Static utility only

    /**
     * Maps a raw ID string and a Command DTO into validated Domain types.
     * Triggers VAL-004 if the galleryUuid format is invalid.
     */
    public static GalleryArchiveCommand toCommand(String galleryUuid, GalleryArchiveDTO dto) {
        DomainGuard.notBlank(galleryUuid, "Gallery UUID Path Variable");

        return new GalleryArchiveCommand(
                new GalleryUuId(UuId.fromString(galleryUuid)),
                dto.toActor()
        );
    }

    /**
     * Internal Command record to pass to the Use Case.
     */
    public record GalleryArchiveCommand(
            GalleryUuId galleryUuId,
            com.github.calhanwynters.dashboard_admin_lead.common.Actor actor
    ) {}
}

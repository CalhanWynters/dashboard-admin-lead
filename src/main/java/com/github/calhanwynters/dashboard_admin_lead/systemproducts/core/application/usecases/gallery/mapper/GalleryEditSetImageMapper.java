package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.dto.GalleryEditSetImageDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

/**
 * Orchestrates the conversion of Gallery/Image collection updates.
 * Bridges the REST path variable and the request payload.
 */
public final class GalleryEditSetImageMapper {

    private GalleryEditSetImageMapper() { } // Static utility only

    /**
     * Maps the raw Gallery path ID and DTO payload into a validated Command.
     * Triggers DomainGuard if the galleryUuid is syntactically invalid.
     */
    public static GalleryEditSetImageCommand toCommand(String galleryUuid, GalleryEditSetImageDTO dto) {
        DomainGuard.notBlank(galleryUuid, "Gallery UUID Path Variable");

        return new GalleryEditSetImageCommand(
                new GalleryUuId(UuId.fromString(galleryUuid)),
                dto.toImageUuId(),
                dto.toActor()
        );
    }

    /**
     * Internal Command record for the Use Case.
     * Groups all required domain types for the Add/Remove operation.
     */
    public record GalleryEditSetImageCommand(
            GalleryUuId galleryUuId,
            ImageUuId imageUuId,
            Actor actor
    ) {}
}

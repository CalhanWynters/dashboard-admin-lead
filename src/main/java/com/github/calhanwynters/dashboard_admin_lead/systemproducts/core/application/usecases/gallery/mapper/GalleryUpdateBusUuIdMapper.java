package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.dto.GalleryUpdateBusUuIdDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryBusinessUuId;

/**
 * Orchestrates the conversion of Gallery Business Identity update requests.
 * Bridges the REST path variable and the new Business ID/Actor payload.
 */
public final class GalleryUpdateBusUuIdMapper {

    private GalleryUpdateBusUuIdMapper() { } // Static utility only

    /**
     * Maps the raw Gallery path ID and DTO payload into a validated Command.
     * Triggers DomainGuard if the galleryUuid or newBusinessUuid format is invalid.
     */
    public static GalleryUpdateBusUuIdCommand toCommand(String galleryUuid, GalleryUpdateBusUuIdDTO dto) {
        DomainGuard.notBlank(galleryUuid, "Gallery UUID Path Variable");

        return new GalleryUpdateBusUuIdCommand(
                new GalleryUuId(UuId.fromString(galleryUuid)),
                dto.toGalleryBusinessUuId(),
                dto.toActor()
        );
    }

    /**
     * Internal Command record for the Use Case.
     * Groups the target Gallery identity, the new Business identity, and the Actor context.
     */
    public record GalleryUpdateBusUuIdCommand(
            GalleryUuId galleryUuId,
            GalleryBusinessUuId newBusinessUuid,
            Actor actor
    ) {}
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;

/**
 * Command representing the intent to update a Gallery's Business UUID.
 * Handled by GalleryUpdateBusUuIdHandler for SOC 2 compliant ID changes.
 */
public record GalleryUpdateBusUuIdCommand(
        GalleryUuId galleryUuId,
        GalleryBusinessUuId newBusinessUuid,
        Actor actor
) {}

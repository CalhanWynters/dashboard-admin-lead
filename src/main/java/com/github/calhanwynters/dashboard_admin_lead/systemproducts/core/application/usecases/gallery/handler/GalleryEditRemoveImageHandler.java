package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command.GalleryRemoveImageCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.dto.GalleryDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.GalleryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for removing an Image from a Gallery collection.
 * Orchestrates domain validation and state transition.
 */
@Service
public class GalleryEditRemoveImageHandler {

    private final GalleryRepository repository;

    public GalleryEditRemoveImageHandler(GalleryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public GalleryDTO handle(GalleryRemoveImageCommand command) {
        // 1. Retrieve existing aggregate
        GalleryAggregate aggregate = repository.findByUuId(command.galleryUuId())
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found: " + command.galleryUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers GalleryBehavior.evaluateImageRemoval (existence checks)
        // and registers ImageRemovedFromGalleryEvent
        aggregate.removeImage(command.imageUuId(), command.actor());

        // 3. Persist and return updated Read-Model
        return GalleryDTO.fromAggregate(repository.save(aggregate));
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command.GalleryAddImageCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.dto.GalleryDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.GalleryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for adding an Image to a Gallery collection.
 * Orchestrates domain validation and SOC 2 compliant audit updates.
 */
@Service
public class GalleryEditAddImageHandler {

    private final GalleryRepository repository;

    public GalleryEditAddImageHandler(GalleryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public GalleryDTO handle(GalleryAddImageCommand command) {
        // 1. Retrieve existing aggregate
        GalleryAggregate aggregate = repository.findByUuId(command.galleryUuId())
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found: " + command.galleryUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers GalleryBehavior.evaluateImageAddition (size limits/auth)
        // and registers ImageAddedToGalleryEvent
        aggregate.addImage(command.imageUuId(), command.actor());

        // 3. Persist and return updated Read-Model
        return GalleryDTO.fromAggregate(repository.save(aggregate));
    }
}

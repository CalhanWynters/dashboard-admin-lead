package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command.GalleryTogglePublicStatusCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.dto.GalleryDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.GalleryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for Gallery visibility toggling.
 * Orchestrates domain validation and state transition via GalleryAggregate.
 */
@Service
public class GalleryTogglePublicStatusHandler {

    private final GalleryRepository repository;

    public GalleryTogglePublicStatusHandler(GalleryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public GalleryDTO handle(GalleryTogglePublicStatusCommand command) {
        // 1. Retrieve existing aggregate via hardened GalleryUuId
        GalleryAggregate aggregate = repository.findByUuId(command.galleryUuId())
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found: " + command.galleryUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers GalleryBehavior.evaluatePublicityChange (Auth/Difference checks)
        // and registers GalleryPublicStatusToggledEvent
        aggregate.togglePublicStatus(command.newPublicStatus(), command.actor());

        // 3. Persist and return updated Read-Model
        return GalleryDTO.fromAggregate(repository.save(aggregate));
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command.GallerySoftDeleteCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.dto.GalleryDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.GalleryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for Gallery soft-deletion.
 * Orchestrates security checks and state transition via GalleryAggregate.
 */
@Service
public class GallerySoftDeleteHandler {

    private final GalleryRepository repository;

    public GallerySoftDeleteHandler(GalleryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public GalleryDTO handle(GallerySoftDeleteCommand command) {
        // 1. Retrieve existing aggregate via hardened GalleryUuId
        GalleryAggregate aggregate = repository.findByUuId(command.galleryUuId())
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found: " + command.galleryUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers ensureActive(), verifyLifecycleAuthority(), and GallerySoftDeletedEvent
        aggregate.softDelete(command.actor());

        // 3. Persist and return updated Read-Model
        return GalleryDTO.fromAggregate(repository.save(aggregate));
    }
}

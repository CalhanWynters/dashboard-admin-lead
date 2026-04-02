package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command.GalleryUnArchiveCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.dto.GalleryDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.GalleryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for Gallery unarchiving.
 * Orchestrates security checks and state transition via GalleryAggregate.
 */
@Service
public class GalleryUnArchiveHandler {

    private final GalleryRepository repository;

    public GalleryUnArchiveHandler(GalleryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public GalleryDTO handle(GalleryUnArchiveCommand command) {
        // 1. Retrieve existing aggregate
        GalleryAggregate aggregate = repository.findByUuId(command.galleryUuId())
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found: " + command.galleryUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers verifyLifecycleAuthority (Manager/Admin required)
        // and registers GalleryUnarchivedEvent
        aggregate.unarchive(command.actor());

        // 3. Persist and return updated Read-Model
        return GalleryDTO.fromAggregate(repository.save(aggregate));
    }
}

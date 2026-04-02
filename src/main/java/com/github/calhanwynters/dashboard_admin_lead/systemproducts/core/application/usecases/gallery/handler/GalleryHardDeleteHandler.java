package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command.GalleryHardDeleteCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.GalleryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for the permanent removal of a Gallery.
 * Enforces Administrator-only access via BaseAggregateRoot and Domain Events.
 */
@Service
public class GalleryHardDeleteHandler {

    private final GalleryRepository repository;

    public GalleryHardDeleteHandler(GalleryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void handle(GalleryHardDeleteCommand command) {
        // 1. Retrieve
        GalleryAggregate aggregate = repository.findByUuId(command.galleryUuId())
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found: " + command.galleryUuId().value()));

        // 2. Invoke Domain Logic (Matches the 1-arg method in your aggregate)
        // This calls executeHardDelete internally with the GalleryHardDeletedEvent
        aggregate.hardDelete(command.actor());

        // 3. Physical Removal
        repository.hardDelete(command.galleryUuId());
    }
}

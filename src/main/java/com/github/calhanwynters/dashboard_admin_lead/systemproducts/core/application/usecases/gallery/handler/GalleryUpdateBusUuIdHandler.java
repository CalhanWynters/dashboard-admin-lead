package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command.GalleryUpdateBusUuIdCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.dto.GalleryDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.GalleryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for Gallery Business Identity updates.
 * Enforces Administrator-only access via BaseAggregateRoot logic.
 */
@Service
public class GalleryUpdateBusUuIdHandler {

    private final GalleryRepository repository;

    public GalleryUpdateBusUuIdHandler(GalleryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public GalleryDTO handle(GalleryUpdateBusUuIdCommand command) {
        // 1. Retrieve existing aggregate via hardened GalleryUuId
        GalleryAggregate aggregate = repository.findByUuId(command.galleryUuId())
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found: " + command.galleryUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers evaluateGenericBusinessIdChange (Admin check + difference check)
        // and registers GalleryBusinessUuIdChangedEvent
        aggregate.updateBusinessUuId(command.newBusinessUuid(), command.actor());

        // 3. Persist and return updated Read-Model
        return GalleryDTO.fromAggregate(repository.save(aggregate));
    }
}

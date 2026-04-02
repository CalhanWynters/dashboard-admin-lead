package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command.GalleryCreateCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.dto.GalleryDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.GalleryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for Gallery initialization.
 * Orchestrates domain factory execution and initial persistence.
 */
@Service
public class GalleryCreateHandler {

    private final GalleryRepository repository;

    public GalleryCreateHandler(GalleryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public GalleryDTO handle(GalleryCreateCommand command) {
        // 1. Invoke Domain Factory
        // Triggers GalleryBehavior.validateCreation and registers GalleryCreatedEvent
        GalleryAggregate aggregate = GalleryAggregate.create(
                command.galleryUuId(),
                command.businessUuId(),
                command.actor()
        );

        // 2. Persist the new Aggregate
        // recordUpdate(actor) in BaseAggregateRoot sets initial AuditMetadata
        GalleryAggregate saved = repository.save(aggregate);

        // 3. Return Read-Model
        return GalleryDTO.fromAggregate(saved);
    }
}

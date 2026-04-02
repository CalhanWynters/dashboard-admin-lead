package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.dto.GalleryDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.mapper.GalleryArchiveMapper.GalleryArchiveCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.GalleryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for archiving a Gallery.
 * Orchestrates security checks and state transition via GalleryAggregate.
 */
@Service
public class GalleryArchiveHandler {

    private final GalleryRepository repository;

    public GalleryArchiveHandler(GalleryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public GalleryDTO handle(GalleryArchiveCommand command) {
        // 1. Retrieve existing aggregate via hardened GalleryUuId
        GalleryAggregate aggregate = repository.findByUuId(command.galleryUuId())
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found: " + command.galleryUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers verifyLifecycleAuthority (Manager/Admin required) and GalleryArchivedEvent
        aggregate.archive(command.actor());

        // 3. Persist and return the updated Read-Model
        return GalleryDTO.fromAggregate(repository.save(aggregate));
    }
}

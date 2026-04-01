package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.command.ImagesSoftDeleteCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.dto.ImagesDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImageAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.ImagesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for soft-deleting an Image.
 * Orchestrates security checks and state transition via ImageAggregate.
 */
@Service
public class ImagesSoftDeleteHandler {

    private final ImagesRepository repository;

    public ImagesSoftDeleteHandler(ImagesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ImagesDTO handle(ImagesSoftDeleteCommand command) {
        // 1. Map raw UUID to Domain Technical ID
        ImageUuId imageUuId = new ImageUuId(UuId.fromString(command.uuid().toString()));

        // 2. Retrieve existing aggregate
        ImageAggregate aggregate = repository.findByUuId(imageUuId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found: " + command.uuid()));

        // 3. Invoke Domain Logic
        // Triggers ensureActive(), verifyLifecycleAuthority(), and ImageSoftDeletedEvent
        aggregate.softDelete(command.actor());

        // 4. Persist and return flattened DTO
        return ImagesDTO.fromAggregate(repository.save(aggregate));
    }
}

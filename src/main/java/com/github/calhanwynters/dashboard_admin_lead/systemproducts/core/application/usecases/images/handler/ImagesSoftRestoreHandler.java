package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.command.ImagesSoftRestoreCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.dto.ImagesDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImageAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.ImagesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for restoring a soft-deleted Image.
 * Restricts restoration to Administrators per BaseAggregateRoot logic.
 */
@Service
public class ImagesSoftRestoreHandler {

    private final ImagesRepository repository;

    public ImagesSoftRestoreHandler(ImagesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ImagesDTO handle(ImagesSoftRestoreCommand command) {
        // 1. Map raw UUID to Domain Technical ID
        ImageUuId imageUuId = new ImageUuId(UuId.fromString(command.uuid().toString()));

        // 2. Retrieve existing aggregate
        ImageAggregate aggregate = repository.findByUuId(imageUuId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found: " + command.uuid()));

        // 3. Invoke Domain Logic
        // Triggers verifyRestorable(actor) which requires ROLE_ADMIN
        aggregate.restore(command.actor());

        // 4. Persist and return flattened DTO
        return ImagesDTO.fromAggregate(repository.save(aggregate));
    }
}

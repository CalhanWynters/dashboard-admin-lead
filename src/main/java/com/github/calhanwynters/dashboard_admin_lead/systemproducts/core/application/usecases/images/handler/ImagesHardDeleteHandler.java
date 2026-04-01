package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.command.ImagesHardDeleteCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImageAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.ImagesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for the permanent removal of an Image.
 * Enforces Administrator-only access via BaseAggregateRoot.
 */
@Service
public class ImagesHardDeleteHandler {

    private final ImagesRepository repository;

    public ImagesHardDeleteHandler(ImagesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void handle(ImagesHardDeleteCommand command) {
        // 1. Map raw UUID to Domain Technical ID
        ImageUuId imageUuId = new ImageUuId(UuId.fromString(command.uuid().toString()));

        // 2. Retrieve existing aggregate
        ImageAggregate aggregate = repository.findByUuId(imageUuId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found: " + command.uuid()));

        // 3. Invoke Domain Logic
        // Triggers verifyHardDeleteAuthority (ROLE_ADMIN required) and registers ImageHardDeletedEvent
        aggregate.hardDelete(command.actor());

        // 4. Physical Removal
        repository.hardDelete(imageUuId);
    }
}

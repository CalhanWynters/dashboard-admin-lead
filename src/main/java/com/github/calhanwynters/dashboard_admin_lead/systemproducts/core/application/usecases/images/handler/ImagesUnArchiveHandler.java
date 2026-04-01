package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.command.ImagesUnArchiveCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.dto.ImagesDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImageAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.ImagesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for unarchiving an Image.
 * Restores the image to an active state for business operations.
 */
@Service
public class ImagesUnArchiveHandler {

    private final ImagesRepository repository;

    public ImagesUnArchiveHandler(ImagesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ImagesDTO handle(ImagesUnArchiveCommand command) {
        // 1. Map raw UUID to Domain Technical ID
        ImageUuId imageUuId = new ImageUuId(UuId.fromString(command.uuid().toString()));

        // 2. Retrieve existing aggregate
        ImageAggregate aggregate = repository.findByUuId(imageUuId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found: " + command.uuid()));

        // 3. Invoke Domain Logic
        // Triggers verifyLifecycleAuthority (Manager/Admin required) and ImageUnarchivedEvent
        aggregate.unarchive(command.actor());

        // 4. Persist and return the updated state
        return ImagesDTO.fromAggregate(repository.save(aggregate));
    }
}

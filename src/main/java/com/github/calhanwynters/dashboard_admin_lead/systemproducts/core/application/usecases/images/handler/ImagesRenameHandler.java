package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.command.ImagesRenameCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.dto.ImagesDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImageAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageName;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.ImagesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for renaming an existing Image.
 * Orchestrates domain validation and audit updates.
 */
@Service
public class ImagesRenameHandler {

    private final ImagesRepository repository;

    public ImagesRenameHandler(ImagesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ImagesDTO handle(ImagesRenameCommand command) {
        // 1. Map raw UUID to Domain Technical ID
        ImageUuId imageUuId = new ImageUuId(UuId.fromString(command.uuid().toString()));

        // 2. Retrieve existing aggregate
        ImageAggregate aggregate = repository.findByUuId(imageUuId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found: " + command.uuid()));

        // 3. Map Command string to Domain ImageName
        // Triggers DomainGuard validation for length and lexical content
        ImageName newName = new ImageName(Name.from(command.newName()));

        // 4. Invoke Domain Logic
        // Triggers ImagesBehavior.evaluateRename and registers ImageNameUpdatedEvent
        aggregate.rename(newName, command.actor());

        // 5. Persist and return flattened DTO
        return ImagesDTO.fromAggregate(repository.save(aggregate));
    }
}

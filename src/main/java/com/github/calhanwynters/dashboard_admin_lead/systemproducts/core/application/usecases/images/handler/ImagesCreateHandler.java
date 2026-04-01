package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.Description;
import com.github.calhanwynters.dashboard_admin_lead.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.command.ImagesCreateCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.dto.ImagesDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImageAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.ImagesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for creating and uploading a new Image.
 * Orchestrates domain initialization and persistence.
 */
@Service
public class ImagesCreateHandler {

    private final ImagesRepository repository;

    public ImagesCreateHandler(ImagesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ImagesDTO handle(ImagesCreateCommand command) {
        // 1. Map raw Command types to Domain Value Objects
        // Triggers DomainGuard validation for UUID, Name, and Description
        ImageUuId uuId = new ImageUuId(UuId.fromString(command.uuid().toString()));
        ImagesBusinessUuId bUuId = new ImagesBusinessUuId(UuId.fromString(command.businessUuid()));
        ImageName name = new ImageName(Name.from(command.name()));
        ImageDescription desc = new ImageDescription(Description.from(command.description()));
        ImageUrl url = ImageUrl.of(command.url());

        // 2. Execute Domain Factory
        // Triggers ImagesBehavior.validateCreation and registers ImageUploadedEvent
        ImageAggregate aggregate = ImageAggregate.create(
                uuId,
                bUuId,
                name,
                desc,
                url,
                command.actor()
        );

        // 3. Persist and return the flattened DTO
        ImageAggregate saved = repository.save(aggregate);
        return ImagesDTO.fromAggregate(saved);
    }
}

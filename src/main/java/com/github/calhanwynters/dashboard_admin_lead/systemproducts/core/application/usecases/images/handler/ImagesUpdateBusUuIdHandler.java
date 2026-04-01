package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.command.ImagesUpdateBusUuIdCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.dto.ImagesDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImageAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImagesBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.ImagesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for updating an Image's Business UUID.
 * Enforces Administrator-only access via BaseAggregateRoot logic.
 */
@Service
public class ImagesUpdateBusUuIdHandler {

    private final ImagesRepository repository;

    public ImagesUpdateBusUuIdHandler(ImagesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ImagesDTO handle(ImagesUpdateBusUuIdCommand command) {
        // 1. Map raw UUID to Domain Technical ID
        ImageUuId imageUuId = new ImageUuId(UuId.fromString(command.uuid().toString()));

        // 2. Retrieve existing aggregate
        ImageAggregate aggregate = repository.findByUuId(imageUuId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found: " + command.uuid()));

        // 3. Map Command string to Domain Value Object
        // Triggers DomainGuard validation for RFC 9562 compliance
        ImagesBusinessUuId newBusId = new ImagesBusinessUuId(UuId.fromString(command.newBusinessUuid()));

        // 4. Invoke Domain Logic
        // Triggers evaluateGenericBusinessIdChange (Admin check + difference check)
        aggregate.updateBusinessUuId(newBusId, command.actor());

        // 5. Persist and return flattened DTO
        return ImagesDTO.fromAggregate(repository.save(aggregate));
    }
}

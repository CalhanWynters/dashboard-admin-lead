package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.command.ImagesUpdateURLCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.dto.ImagesDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImageAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUrl;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.ImagesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for updating an Image's Source URL.
 * Orchestrates domain validation and SOC 2 compliant audit updates.
 */
@Service
public class ImagesUpdateURLHandler {

    private final ImagesRepository repository;

    public ImagesUpdateURLHandler(ImagesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ImagesDTO handle(ImagesUpdateURLCommand command) {
        // 1. Map raw UUID to Domain Technical ID
        ImageUuId imageUuId = new ImageUuId(UuId.fromString(command.uuid().toString()));

        // 2. Retrieve existing aggregate
        ImageAggregate aggregate = repository.findByUuId(imageUuId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found: " + command.uuid()));

        // 3. Map Command string to Domain ImageUrl
        // (ImageUrl record handles its own validation or wraps a URL VO)
        ImageUrl newUrl = ImageUrl.of(command.newUrl());

        // 4. Invoke Domain Logic
        // Triggers ImagesBehavior.evaluateUrlUpdate and registers ImageUrlUpdatedEvent
        aggregate.updateUrl(newUrl, command.actor());

        // 5. Persist and return flattened DTO
        return ImagesDTO.fromAggregate(repository.save(aggregate));
    }
}

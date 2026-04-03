package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.command.VariantsSoftDeleteCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.VariantsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for soft-deleting a Variant.
 * Orchestrates lifecycle state transitions and event emission via VariantsAggregate.
 */
@Service
public class VariantsSoftDeleteHandler {

    private final VariantsRepository repository;

    public VariantsSoftDeleteHandler(VariantsRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public VariantsDTO handle(VariantsSoftDeleteCommand command) {
        // 1. Map raw UUID to Domain Technical ID
        VariantsUuId variantsUuId = new VariantsUuId(UuId.fromString(command.targetUuId().toString()));

        // 2. Retrieve existing aggregate
        VariantsAggregate aggregate = repository.findByUuId(variantsUuId)
                .orElseThrow(() -> new IllegalArgumentException("Variant not found: " + command.targetUuId()));

        // 3. Invoke Domain Logic
        // Triggers LifecycleState change and registers VariantSoftDeletedEvent
        aggregate.softDelete(command.data().toActor());

        // 4. Persist and return flattened DTO
        return VariantsDTO.fromAggregate(repository.save(aggregate));
    }
}

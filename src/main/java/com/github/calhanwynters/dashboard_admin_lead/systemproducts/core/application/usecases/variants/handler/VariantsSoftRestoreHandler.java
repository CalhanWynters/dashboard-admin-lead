package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.command.VariantsSoftRestoreCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.VariantsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for restoring a soft-deleted Variant.
 * Orchestrates recovery and event emission via VariantsAggregate for SOC 2 accountability.
 */
@Service
public class VariantsSoftRestoreHandler {

    private final VariantsRepository repository;

    public VariantsSoftRestoreHandler(VariantsRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public VariantsDTO handle(VariantsSoftRestoreCommand command) {
        // 1. Map raw UUID to Domain Technical ID
        VariantsUuId variantsUuId = new VariantsUuId(UuId.fromString(command.targetUuId().toString()));

        // 2. Retrieve existing aggregate
        VariantsAggregate aggregate = repository.findByUuId(variantsUuId)
                .orElseThrow(() -> new IllegalArgumentException("Variant not found: " + command.targetUuId()));

        // 3. Invoke Domain Logic
        // Reverses the soft-delete state and registers VariantRestoredEvent
        aggregate.restore(command.toActor());

        // 4. Persist and return flattened DTO
        return VariantsDTO.fromAggregate(repository.save(aggregate));
    }
}

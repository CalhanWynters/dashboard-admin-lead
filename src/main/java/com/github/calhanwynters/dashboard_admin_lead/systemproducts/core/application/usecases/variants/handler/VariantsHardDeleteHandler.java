package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.command.VariantsHardDeleteCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.VariantsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for the permanent removal of a Variant.
 * Orchestrates final authorization and repository purge for SOC 2 data disposal compliance.
 */
@Service
public class VariantsHardDeleteHandler {

    private final VariantsRepository repository;

    public VariantsHardDeleteHandler(VariantsRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void handle(VariantsHardDeleteCommand command) {
        // 1. Map raw UUID to Domain Technical ID
        VariantsUuId variantsUuId = new VariantsUuId(UuId.fromString(command.targetUuId().toString()));

        // 2. Retrieve existing aggregate
        VariantsAggregate aggregate = repository.findByUuId(variantsUuId)
                .orElseThrow(() -> new IllegalArgumentException("Variant not found for hard deletion: " + command.targetUuId()));

        // 3. Invoke Domain Logic
        // Triggers final authorization check and emits VariantHardDeletedEvent before destruction
        aggregate.hardDelete(command.toActor());

        // 4. Execute permanent disposal via the Aggregate (Audit-Before-Purge pattern)
        repository.hardDelete(aggregate);
    }
}

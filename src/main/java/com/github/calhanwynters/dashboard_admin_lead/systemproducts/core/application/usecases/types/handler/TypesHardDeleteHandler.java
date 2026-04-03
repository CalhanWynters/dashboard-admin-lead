package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.command.TypesHardDeleteCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.TypesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for the permanent removal of a Product Type.
 * Orchestrates final authorization and repository purge for SOC 2 data disposal compliance.
 */
@Service
public class TypesHardDeleteHandler {

    private final TypesRepository repository;

    public TypesHardDeleteHandler(TypesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void handle(TypesHardDeleteCommand command) {
        // 1. Map raw UUID to Domain Technical ID
        TypesUuId typesUuId = new TypesUuId(UuId.fromString(command.targetUuId().toString()));

        // 2. Retrieve existing aggregate
        TypesAggregate aggregate = repository.findByUuId(typesUuId)
                .orElseThrow(() -> new IllegalArgumentException("Product Type not found for hard deletion: " + command.targetUuId()));

        // 3. Invoke Domain Logic (Triggers authorization and emits events)
        // This generates the TypeHardDeletedEvent for the audit trail before the record is gone.
        aggregate.hardDelete(command.toActor());

        // 4. Execute permanent disposal
        // Passing the full aggregate to match the updated TypesRepository interface
        repository.hardDelete(aggregate);
    }

}

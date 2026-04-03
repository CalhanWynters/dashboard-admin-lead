package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.command.TypesUnArchiveCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypesDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.TypesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for unarchiving a Product Type.
 * Orchestrates the recovery transition via TypesAggregate for SOC 2 lifecycle accountability.
 */
@Service
public class TypesUnArchiveHandler {

    private final TypesRepository repository;

    public TypesUnArchiveHandler(TypesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TypesDTO handle(TypesUnArchiveCommand command) {
        // 1. Map raw UUID to Domain Technical ID
        TypesUuId typesUuId = new TypesUuId(UuId.fromString(command.targetUuId().toString()));

        // 2. Retrieve existing aggregate
        TypesAggregate aggregate = repository.findByUuId(typesUuId)
                .orElseThrow(() -> new IllegalArgumentException("Product Type not found: " + command.targetUuId()));

        // 3. Invoke Domain Logic
        // Triggers state transition and emits TypeUnarchivedEvent
        aggregate.unarchive(command.toActor());

        // 4. Persist and return flattened DTO
        return TypesDTO.fromAggregate(repository.save(aggregate));
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.command.TypesArchiveCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypesDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.TypesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for archiving a Product Type.
 * Orchestrates the transition to a read-only state via TypesAggregate for SOC 2 lifecycle compliance.
 */
@Service
public class TypesArchiveHandler {

    private final TypesRepository repository;

    public TypesArchiveHandler(TypesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TypesDTO handle(TypesArchiveCommand command) {
        // 1. Map raw UUID to Domain Technical ID
        TypesUuId typesUuId = new TypesUuId(UuId.fromString(command.targetUuId().toString()));

        // 2. Retrieve existing aggregate
        TypesAggregate aggregate = repository.findByUuId(typesUuId)
                .orElseThrow(() -> new IllegalArgumentException("Product Type not found: " + command.targetUuId()));

        // 3. Invoke Domain Logic
        // Triggers lifecycle state transition and emits TypeArchivedEvent
        aggregate.archive(command.data().toActor());

        // 4. Persist and return flattened DTO
        return TypesDTO.fromAggregate(repository.save(aggregate));
    }
}

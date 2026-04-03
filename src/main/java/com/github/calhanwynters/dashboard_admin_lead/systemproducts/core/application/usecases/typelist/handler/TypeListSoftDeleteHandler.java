package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command.TypeListSoftDeleteCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.dto.TypeListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.TypeListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for soft-deleting a TypeList.
 * Orchestrates lifecycle state transitions and event emission for SOC 2 accountability.
 */
@Service
public class TypeListSoftDeleteHandler {

    private final TypeListRepository repository;

    public TypeListSoftDeleteHandler(TypeListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TypeListDTO handle(TypeListSoftDeleteCommand command) {
        // 1. Retrieve the existing aggregate
        TypeListAggregate aggregate = repository.findByUuId(command.typeListUuId())
                .orElseThrow(() -> new IllegalArgumentException("TypeList not found: " + command.typeListUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers LifecycleState change and registers TypeListSoftDeletedEvent
        aggregate.softDelete(command.actor());

        // 3. Persist and return the updated DTO
        return TypeListDTO.fromAggregate(repository.save(aggregate));
    }
}

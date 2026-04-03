package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command.TypeListSoftRestoreCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.dto.TypeListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.TypeListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for restoring a soft-deleted TypeList.
 * Orchestrates recovery and event emission via TypeListAggregate.
 */
@Service
public class TypeListSoftRestoreHandler {

    private final TypeListRepository repository;

    public TypeListSoftRestoreHandler(TypeListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TypeListDTO handle(TypeListSoftRestoreCommand command) {
        // 1. Retrieve the existing aggregate
        TypeListAggregate aggregate = repository.findByUuId(command.typeListUuId())
                .orElseThrow(() -> new IllegalArgumentException("TypeList not found: " + command.typeListUuId().value()));

        // 2. Invoke Domain Logic
        // Reverses the soft-delete state and registers TypeListRestoredEvent
        aggregate.restore(command.actor());

        // 3. Persist and return the updated DTO
        return TypeListDTO.fromAggregate(repository.save(aggregate));
    }
}

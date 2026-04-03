package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command.TypeListArchiveCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.dto.TypeListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.TypeListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for archiving a TypeList.
 * Orchestrates the read-only state transition via TypeListAggregate.
 */
@Service
public class TypeListArchiveHandler {

    private final TypeListRepository repository;

    public TypeListArchiveHandler(TypeListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TypeListDTO handle(TypeListArchiveCommand command) {
        // 1. Retrieve the existing aggregate
        TypeListAggregate aggregate = repository.findByUuId(command.typeListUuId())
                .orElseThrow(() -> new IllegalArgumentException("TypeList not found: " + command.typeListUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers lifecycle state change and TypeListArchivedEvent
        aggregate.archive(command.actor());

        // 3. Persist and return the updated DTO
        return TypeListDTO.fromAggregate(repository.save(aggregate));
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command.TypeListUnArchiveCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.dto.TypeListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.TypeListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for unarchiving a TypeList.
 * Orchestrates the recovery transition via TypeListAggregate for SOC 2 accountability.
 */
@Service
public class TypeListUnArchiveHandler {

    private final TypeListRepository repository;

    public TypeListUnArchiveHandler(TypeListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TypeListDTO handle(TypeListUnArchiveCommand command) {
        // 1. Retrieve existing aggregate
        TypeListAggregate aggregate = repository.findByUuId(command.typeListUuId())
                .orElseThrow(() -> new IllegalArgumentException("TypeList not found: " + command.typeListUuId().value()));

        // 2. Invoke Domain Logic
        // Reverses archived state and emits TypeListUnarchivedEvent
        aggregate.unarchive(command.actor());

        // 3. Persist and return DTO
        return TypeListDTO.fromAggregate(repository.save(aggregate));
    }
}

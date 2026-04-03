package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command.TypeListUpdateBusUuIdCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.dto.TypeListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.TypeListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for updating a TypeList's Business UUID.
 * Orchestrates ownership changes and event emission via TypeListAggregate.
 */
@Service
public class TypeListUpdateBusUuIdHandler {

    private final TypeListRepository repository;

    public TypeListUpdateBusUuIdHandler(TypeListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TypeListDTO handle(TypeListUpdateBusUuIdCommand command) {
        // 1. Retrieve existing aggregate
        TypeListAggregate aggregate = repository.findByUuId(command.typeListUuId())
                .orElseThrow(() -> new IllegalArgumentException("TypeList not found: " + command.typeListUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers validation and emits TypeListBusinessUuIdChangedEvent
        aggregate.updateBusinessUuId(command.newBusinessUuid(), command.actor());

        // 3. Persist and return DTO
        return TypeListDTO.fromAggregate(repository.save(aggregate));
    }
}

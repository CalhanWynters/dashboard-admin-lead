package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command.TypeListDetachTypeCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.dto.TypeListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.TypeListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for detaching a Product Type from a TypeList.
 * Orchestrates membership subtraction and event emission for SOC 2 integrity.
 */
@Service
public class TypeListRemoveTypeHandler {

    private final TypeListRepository repository;

    public TypeListRemoveTypeHandler(TypeListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TypeListDTO handle(TypeListDetachTypeCommand command) {
        // 1. Retrieve the existing aggregate
        TypeListAggregate aggregate = repository.findByUuId(command.typeListUuId())
                .orElseThrow(() -> new IllegalArgumentException("TypeList not found: " + command.typeListUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers TypeListBehavior checks (ensuring it exists in the set) and TypeDetachedEvent
        aggregate.detachType(command.typesUuId(), command.actor());

        // 3. Persist and return the updated DTO
        return TypeListDTO.fromAggregate(repository.save(aggregate));
    }
}

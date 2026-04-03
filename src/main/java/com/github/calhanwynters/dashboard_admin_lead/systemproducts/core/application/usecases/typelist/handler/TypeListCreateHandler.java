package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command.TypeListCreateCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.dto.TypeListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.TypeListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for creating a new TypeList.
 * Orchestrates initial validation and aggregate instantiation for SOC 2 compliance.
 */
@Service
public class TypeListCreateHandler {

    private final TypeListRepository repository;

    public TypeListCreateHandler(TypeListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TypeListDTO handle(TypeListCreateCommand command) {
        // 1. Invoke Domain Factory
        // Triggers TypeListBehavior validation and registers TypeListCreatedEvent
        TypeListAggregate aggregate = TypeListAggregate.create(
                command.typeListUuId(),
                command.businessUuId(),
                command.actor()
        );

        // 2. Persist the new collection
        // Atomically commits the record and the creation event
        TypeListAggregate saved = repository.save(aggregate);

        // 3. Return flattened DTO
        return TypeListDTO.fromAggregate(saved);
    }
}

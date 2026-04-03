package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command.TypeListHardDeleteCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.TypeListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for the permanent removal of a TypeList.
 * Orchestrates final authorization and repository purge for SOC 2 compliance.
 */
@Service
public class TypeListHardDeleteHandler {

    private final TypeListRepository repository;

    public TypeListHardDeleteHandler(TypeListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void handle(TypeListHardDeleteCommand command) {
        // 1. Retrieve existing aggregate
        TypeListAggregate aggregate = repository.findByUuId(command.typeListUuId())
                .orElseThrow(() -> new IllegalArgumentException("TypeList not found: " + command.typeListUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers elevated authority check and emits TypeListHardDeletedEvent
        aggregate.hardDelete(command.actor());

        // 3. Execute permanent disposal
        repository.hardDelete(command.typeListUuId());
    }
}

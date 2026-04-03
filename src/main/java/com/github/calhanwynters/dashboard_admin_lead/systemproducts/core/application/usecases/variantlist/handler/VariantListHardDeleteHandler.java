package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.command.VariantListHardDeleteCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.VariantListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for the permanent removal of a VariantList.
 * Orchestrates final authorization and repository purge for SOC 2 compliance.
 */
@Service
public class VariantListHardDeleteHandler {

    private final VariantListRepository repository;

    public VariantListHardDeleteHandler(VariantListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void handle(VariantListHardDeleteCommand command) {
        // 1. Retrieve existing aggregate
        VariantListAggregate aggregate = repository.findByUuId(command.variantListUuId())
                .orElseThrow(() -> new IllegalArgumentException("VariantList not found: " + command.variantListUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers elevated authority check and emits VariantListHardDeletedEvent
        aggregate.hardDelete(command.actor());

        // 3. Execute permanent disposal from the persistence layer
        repository.hardDelete(aggregate);
    }
}

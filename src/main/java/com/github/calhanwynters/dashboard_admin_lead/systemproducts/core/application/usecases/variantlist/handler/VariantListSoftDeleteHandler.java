package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.command.VariantListSoftDeleteCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.dto.VariantListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.VariantListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for soft-deleting a VariantList.
 * Orchestrates lifecycle state transitions and event emission for SOC 2 accountability.
 */
@Service
public class VariantListSoftDeleteHandler {

    private final VariantListRepository repository;

    public VariantListSoftDeleteHandler(VariantListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public VariantListDTO handle(VariantListSoftDeleteCommand command) {
        // 1. Retrieve the existing aggregate
        VariantListAggregate aggregate = repository.findByUuId(command.variantListUuId())
                .orElseThrow(() -> new IllegalArgumentException("VariantList not found: " + command.variantListUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers LifecycleState change and registers VariantListSoftDeletedEvent
        aggregate.softDelete(command.actor());

        // 3. Persist and return the updated DTO
        return VariantListDTO.fromAggregate(repository.save(aggregate));
    }
}

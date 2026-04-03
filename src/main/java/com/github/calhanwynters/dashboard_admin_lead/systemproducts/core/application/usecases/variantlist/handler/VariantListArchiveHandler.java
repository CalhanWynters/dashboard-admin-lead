package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.command.VariantListArchiveCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.dto.VariantListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.VariantListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for archiving a VariantList.
 * Orchestrates the read-only state transition via VariantListAggregate.
 */
@Service
public class VariantListArchiveHandler {

    private final VariantListRepository repository;

    public VariantListArchiveHandler(VariantListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public VariantListDTO handle(VariantListArchiveCommand command) {
        // 1. Retrieve the existing aggregate
        VariantListAggregate aggregate = repository.findByUuId(command.variantListUuId())
                .orElseThrow(() -> new IllegalArgumentException("VariantList not found: " + command.variantListUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers lifecycle state change and VariantListArchivedEvent
        aggregate.archive(command.actor());

        // 3. Persist and return the updated DTO
        return VariantListDTO.fromAggregate(repository.save(aggregate));
    }
}

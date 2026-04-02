package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command.PriceListSoftDeleteCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.dto.PriceListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.PriceListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for PriceList soft-deletion.
 * Orchestrates security checks and state transition via PriceListAggregate.
 */
@Service
public class PriceListSoftDeleteHandler {

    private final PriceListRepository repository;

    public PriceListSoftDeleteHandler(PriceListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PriceListDTO handle(PriceListSoftDeleteCommand command) {
        // 1. Retrieve existing aggregate via hardened PriceListUuId
        PriceListAggregate aggregate = repository.findByUuId(command.priceListUuId())
                .orElseThrow(() -> new IllegalArgumentException("PriceList not found: " + command.priceListUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers ensureActive(), verifyLifecycleAuthority(), and PriceListSoftDeletedEvent
        aggregate.softDelete(command.actor());

        // 3. Persist and return the updated Read-Model
        return PriceListDTO.fromAggregate(repository.save(aggregate));
    }
}

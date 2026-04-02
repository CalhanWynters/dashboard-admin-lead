package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command.PriceListUnArchiveCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.dto.PriceListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.PriceListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for PriceList unarchiving.
 * Orchestrates security checks and state transition via PriceListAggregate.
 */
@Service
public class PriceListUnArchiveHandler {

    private final PriceListRepository repository;

    public PriceListUnArchiveHandler(PriceListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PriceListDTO handle(PriceListUnArchiveCommand command) {
        // 1. Retrieve existing aggregate via hardened PriceListUuId
        PriceListAggregate aggregate = repository.findByUuId(command.priceListUuId())
                .orElseThrow(() -> new IllegalArgumentException("PriceList not found: " + command.priceListUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers verifyLifecycleAuthority (Manager/Admin required)
        // and registers PriceListUnarchivedEvent
        aggregate.unarchive(command.actor());

        // 3. Persist and return the updated Read-Model
        return PriceListDTO.fromAggregate(repository.save(aggregate));
    }
}

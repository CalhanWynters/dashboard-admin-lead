package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command.PriceListDeactivateCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.dto.PriceListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.PriceListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for PriceList deactivation.
 * Orchestrates the transition to an operational 'false' state for business use.
 */
@Service
public class PriceListDeactivateHandler {

    private final PriceListRepository repository;

    public PriceListDeactivateHandler(PriceListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PriceListDTO handle(PriceListDeactivateCommand command) {
        // 1. Retrieve existing aggregate via hardened PriceListUuId
        PriceListAggregate aggregate = repository.findByUuId(command.priceListUuId())
                .orElseThrow(() -> new IllegalArgumentException("PriceList not found: " + command.priceListUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers verifyLifecycleAuthority (Manager/Admin required)
        // and registers PriceListDeactivatedEvent
        aggregate.toggleActivation(false, command.actor());

        // 3. Persist and return the updated Read-Model
        return PriceListDTO.fromAggregate(repository.save(aggregate));
    }
}

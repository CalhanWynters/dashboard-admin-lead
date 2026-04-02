package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command.PriceListUpdateBusUuIdCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.dto.PriceListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.PriceListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for PriceList Business Identity updates.
 * Enforces Administrator-only access via BaseAggregateRoot logic.
 */
@Service
public class PriceListUpdateBusUuIdHandler {

    private final PriceListRepository repository;

    public PriceListUpdateBusUuIdHandler(PriceListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PriceListDTO handle(PriceListUpdateBusUuIdCommand command) {
        // 1. Retrieve existing aggregate via hardened PriceListUuId
        PriceListAggregate aggregate = repository.findByUuId(command.priceListUuId())
                .orElseThrow(() -> new IllegalArgumentException("PriceList not found: " + command.priceListUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers evaluateGenericBusinessIdChange (Admin check + difference check)
        // and registers PriceListBusinessUuIdChangedEvent
        aggregate.updateBusinessUuId(command.newBusinessUuid(), command.actor());

        // 3. Persist and return the updated Read-Model
        return PriceListDTO.fromAggregate(repository.save(aggregate));
    }
}

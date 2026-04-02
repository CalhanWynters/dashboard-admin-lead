package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command.PriceListHardDeleteCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.PriceListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for the permanent removal of a PriceList.
 * Enforces Administrator-only access via BaseAggregateRoot security.
 */
@Service
public class PriceListHardDeleteHandler {

    private final PriceListRepository repository;

    public PriceListHardDeleteHandler(PriceListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void handle(PriceListHardDeleteCommand command) {
        // 1. Retrieve existing aggregate via hardened PriceListUuId
        PriceListAggregate aggregate = repository.findByUuId(command.priceListUuId())
                .orElseThrow(() -> new IllegalArgumentException("PriceList not found: " + command.priceListUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers verifyHardDeleteAuthority (ROLE_ADMIN required)
        // and registers PriceListHardDeletedEvent
        aggregate.hardDelete(command.actor());

        // 3. Physical Removal from Persistence
        repository.hardDelete(command.priceListUuId());
    }
}

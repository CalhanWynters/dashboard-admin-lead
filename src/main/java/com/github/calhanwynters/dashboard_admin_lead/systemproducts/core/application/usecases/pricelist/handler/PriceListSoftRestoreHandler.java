package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command.PriceListSoftRestoreCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.dto.PriceListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.PriceListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for restoring a soft-deleted PriceList.
 * Restricts restoration to Administrators per BaseAggregateRoot logic.
 */
@Service
public class PriceListSoftRestoreHandler {

    private final PriceListRepository repository;

    public PriceListSoftRestoreHandler(PriceListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PriceListDTO handle(PriceListSoftRestoreCommand command) {
        // 1. Retrieve existing aggregate via hardened PriceListUuId
        PriceListAggregate aggregate = repository.findByUuId(command.priceListUuId())
                .orElseThrow(() -> new IllegalArgumentException("PriceList not found: " + command.priceListUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers verifyRestorable(actor) (Admin required) and PriceListRestoredEvent
        aggregate.restore(command.actor());

        // 3. Persist and return the updated Read-Model
        return PriceListDTO.fromAggregate(repository.save(aggregate));
    }
}

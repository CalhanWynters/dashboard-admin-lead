package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command.PriceListCreateCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.dto.PriceListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.PriceListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for PriceList initialization.
 * Orchestrates domain factory execution and initial SOC 2 audit setup.
 */
@Service
public class PriceListCreateHandler {

    private final PriceListRepository repository;

    public PriceListCreateHandler(PriceListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PriceListDTO handle(PriceListCreateCommand command) {
        // 1. Execute Domain Factory
        // Triggers PriceListFactory.create and registers PriceListCreatedEvent
        PriceListAggregate aggregate = PriceListAggregate.create(
                command.priceListUuId(),
                command.businessUuId(),
                command.strategy(),
                command.actor()
        );

        // 2. Persist the new Aggregate
        // BaseAggregateRoot.recordUpdate(actor) sets the initial 'Created By' metadata
        PriceListAggregate saved = repository.save(aggregate);

        // 3. Return the Read-Model DTO
        return PriceListDTO.fromAggregate(saved);
    }
}

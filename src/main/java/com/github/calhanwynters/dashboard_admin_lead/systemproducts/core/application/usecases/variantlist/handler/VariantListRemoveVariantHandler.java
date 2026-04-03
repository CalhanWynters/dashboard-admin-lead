package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.command.VariantListDetachVariantCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.dto.VariantListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.VariantListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for detaching a Variant from a VariantList.
 * Orchestrates membership subtraction and event emission for SOC 2 integrity.
 */
@Service
public class VariantListRemoveVariantHandler {

    private final VariantListRepository repository;

    public VariantListRemoveVariantHandler(VariantListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public VariantListDTO handle(VariantListDetachVariantCommand command) {
        // 1. Retrieve the existing aggregate
        VariantListAggregate aggregate = repository.findByUuId(command.variantListUuId())
                .orElseThrow(() -> new IllegalArgumentException("VariantList not found: " + command.variantListUuId().value()));

        // 2. Invoke Domain Logic
        // Triggers VariantListBehavior checks (ensuring membership) and VariantDetachedEvent
        aggregate.detachVariant(command.variantsUuId(), command.actor());

        // 3. Persist and return the updated DTO
        return VariantListDTO.fromAggregate(repository.save(aggregate));
    }
}

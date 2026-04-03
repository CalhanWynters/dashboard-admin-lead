package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.command.VariantListCreateCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.dto.VariantListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.VariantListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for creating a new VariantList.
 * Orchestrates raw input hardening and aggregate instantiation for SOC 2 compliance.
 */
@Service
public class VariantListCreateHandler {

    private final VariantListRepository repository;

    public VariantListCreateHandler(VariantListRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public VariantListDTO handle(VariantListCreateCommand command) {
        // 1. Generate the Identity for the new Aggregate
        VariantListUuId newListUuId = VariantListUuId.generate();

        // 2. Harden raw business ID
        VariantListBusinessUuId businessUuId = new VariantListBusinessUuId(UuId.fromString(command.businessUuid()));

        // 3. Invoke Domain Factory (Matching your signature: UuId, BusinessUuId, Actor)
        VariantListAggregate aggregate = VariantListAggregate.create(
                newListUuId,
                businessUuId,
                command.actor()
        );

        // 4. Attach initial variants if present
        if (command.variantUuids() != null && !command.variantUuids().isEmpty()) {
            command.variantUuids().stream()
                    .map(uuidStr -> new VariantsUuId(UuId.fromString(uuidStr)))
                    .forEach(vUuId -> aggregate.attachVariant(vUuId, command.actor()));
        }

        // 5. Persist and Return
        return VariantListDTO.fromAggregate(repository.save(aggregate));
    }

}

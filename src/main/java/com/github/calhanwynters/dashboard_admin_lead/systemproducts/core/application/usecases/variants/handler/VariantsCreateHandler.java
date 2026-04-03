package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.command.VariantsCreateCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.VariantsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;

/**
 * Handler for creating a new Variant.
 * Orchestrates initial validation and aggregate instantiation for SOC 2 compliance.
 */
@Service
public class VariantsCreateHandler {

    private final VariantsRepository repository;

    public VariantsCreateHandler(VariantsRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public VariantsDTO handle(VariantsCreateCommand command) {
        // 1. Map DTO data to Hardened Domain Wrappers
        VariantsUuId uuId = new VariantsUuId(command.data().uuid() != null ?
                UuId.fromString(command.data().uuid().toString()) :
                UuId.generate());

        VariantsBusinessUuId bUuId = new VariantsBusinessUuId(
                UuId.fromString(command.data().businessUuid()));

        VariantsName name = new VariantsName(
                com.github.calhanwynters.dashboard_admin_lead.common.Name.from(command.data().name()));

        // 2. Invoke Domain Factory
        // Triggers VariantsBehavior.validateCreation and registers VariantCreatedEvent
        VariantsAggregate aggregate = VariantsAggregate.create(
                uuId, bUuId, name, command.toActor()
        );

        // 3. Persist and return the finalized DTO
        return VariantsDTO.fromAggregate(repository.save(aggregate));
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.command.TypeUpdatePhysicalSpecsCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypesDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.TypesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for editing Product Type Physical Specifications.
 * Orchestrates the transition from raw DTO inputs to hardened Domain Aggregate updates.
 */
@Service
public class TypeEditPhysicalSpecsHandler {

    private final TypesRepository repository;

    public TypeEditPhysicalSpecsHandler(TypesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TypesDTO handle(TypeUpdatePhysicalSpecsCommand command) {
        // 1. Map raw UUID to Domain Technical ID
        TypesUuId typesUuId = new TypesUuId(UuId.fromString(command.targetUuId().toString()));

        // 2. Retrieve existing aggregate
        TypesAggregate aggregate = repository.findByUuId(typesUuId)
                .orElseThrow(() -> new IllegalArgumentException("Product Type not found: " + command.targetUuId()));

        // 3. Invoke Domain Logic via DTO conversion
        // Triggers change detection (Dimension/Weight shifts) and event registration
        aggregate.updatePhysicalSpecs(
                command.data().toTypesPhysicalSpecs(),
                command.data().toActor()
        );

        // 4. Persist changes and return the finalized DTO
        return TypesDTO.fromAggregate(repository.save(aggregate));
    }
}

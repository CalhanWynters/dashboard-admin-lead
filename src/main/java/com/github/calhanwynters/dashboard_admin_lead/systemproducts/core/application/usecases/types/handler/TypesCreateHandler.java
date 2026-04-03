package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.PhysicalSpecs;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.command.TypesCreateCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypesDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.TypesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;

/**
 * Handler for creating a new Product Type.
 * Orchestrates initial validation and aggregate instantiation for SOC 2 compliance.
 */
@Service
public class TypesCreateHandler {

    private final TypesRepository repository;

    public TypesCreateHandler(TypesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TypesDTO handle(TypesCreateCommand command) {
        // 1. Map DTO data to Hardened Domain Wrappers
        TypesUuId uuId = new TypesUuId(command.data().uuid() != null ?
                com.github.calhanwynters.dashboard_admin_lead.common.UuId.fromString(command.data().uuid().toString()) :
                com.github.calhanwynters.dashboard_admin_lead.common.UuId.generate());

        TypesBusinessUuId bUuId = new TypesBusinessUuId(
                com.github.calhanwynters.dashboard_admin_lead.common.UuId.fromString(command.data().businessUuid()));

        TypesName name = new TypesName(
                com.github.calhanwynters.dashboard_admin_lead.common.Name.from(command.data().name()));

        // 2. Map Physical Specs (utilizing the NONE pattern if null)
        TypesPhysicalSpecs specs = command.data().specs() != null ?
                new TypesPhysicalSpecs(new PhysicalSpecs(
                        new com.github.calhanwynters.dashboard_admin_lead.common.Weight(
                                command.data().specs().weightAmount(),
                                com.github.calhanwynters.dashboard_admin_lead.common.WeightUnitEnums.valueOf(command.data().specs().weightUnit().toUpperCase())),
                        new com.github.calhanwynters.dashboard_admin_lead.common.Dimensions(
                                command.data().specs().length(), command.data().specs().width(), command.data().specs().height(),
                                com.github.calhanwynters.dashboard_admin_lead.common.DimensionUnitEnums.valueOf(command.data().specs().sizeUnit().toUpperCase())),
                        new com.github.calhanwynters.dashboard_admin_lead.common.CareInstruction(command.data().specs().careInstructions())
                )) : TypesPhysicalSpecs.NONE;

        // 3. Invoke Domain Factory
        // Triggers validation and registers TypeCreatedEvent
        TypesAggregate aggregate = TypesAggregate.create(
                uuId, bUuId, name, specs, command.toActor()
        );

        // 4. Persist and return the finalized DTO
        return TypesDTO.fromAggregate(repository.save(aggregate));
    }
}

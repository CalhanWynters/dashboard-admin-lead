package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.command.VariantsAssignFeatureCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.VariantsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for assigning a Feature to a Variant.
 * Orchestrates membership validation and event emission for SOC 2 integrity.
 */
@Service
public class VariantsAddFeatureHandler {

    private final VariantsRepository repository;

    public VariantsAddFeatureHandler(VariantsRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public VariantsDTO handle(VariantsAssignFeatureCommand command) {
        // 1. Map raw UUID to Domain Technical ID
        VariantsUuId variantsUuId = new VariantsUuId(UuId.fromString(command.targetUuId().toString()));

        // 2. Retrieve existing aggregate
        VariantsAggregate aggregate = repository.findByUuId(variantsUuId)
                .orElseThrow(() -> new IllegalArgumentException("Variant not found: " + command.targetUuId()));

        // 3. Invoke Domain Logic
        // Triggers VariantsBehavior.ensureCanAssign and FeatureAssignedEvent registration
        aggregate.assignFeature(
                command.data().toFeatureUuId(),
                command.data().toActor()
        );

        // 4. Persist and return flattened DTO
        return VariantsDTO.fromAggregate(repository.save(aggregate));
    }
}

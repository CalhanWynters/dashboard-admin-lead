package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto.FeatureDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto.FeatureUpdateBusUuIdDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.mapper.FeatureUpdateBusUuIdMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.FeaturesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Handler for updating a Feature's Business UUID.
 * Enforces Administrator-only access via BaseAggregateRoot.
 */
@Service
public class FeatureUpdateBusUuIdHandler {

    private final FeaturesRepository repository;

    public FeatureUpdateBusUuIdHandler(FeaturesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FeatureDTO handle(UUID uuid, FeatureUpdateBusUuIdDTO dto) {
        // 1. Map raw UUID to Domain Technical ID
        FeatureUuId featureUuId = new FeatureUuId(UuId.fromString(uuid.toString()));

        // 2. Retrieve existing aggregate
        FeaturesAggregate aggregate = repository.findByUuId(featureUuId)
                .orElseThrow(() -> new IllegalArgumentException("Feature not found: " + uuid));

        // 3. Map DTO to Domain Types via Mapper
        FeatureBusinessUuId newBusId = FeatureUpdateBusUuIdMapper.toDomainBusinessUuId(dto);
        Actor actor = FeatureUpdateBusUuIdMapper.toActor(dto);

        // 4. Invoke Domain Logic
        // Triggers evaluateGenericBusinessIdChange (Admin check + difference check)
        aggregate.updateBusinessUuId(newBusId, actor);

        // 5. Persist and return
        return FeatureDTO.fromAggregate(repository.save(aggregate));
    }
}

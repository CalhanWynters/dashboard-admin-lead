package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto.FeatureDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto.FeaturesArchiveDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.mapper.FeaturesArchiveMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.FeaturesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Handler for archiving a Feature.
 * Orchestrates security checks and state transition via BaseAggregateRoot.
 */
@Service
public class FeaturesArchiveHandler {

    private final FeaturesRepository repository;

    public FeaturesArchiveHandler(FeaturesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FeatureDTO handle(UUID uuid, FeaturesArchiveDTO dto) {
        // 1. Map raw UUID to Domain Value Object
        FeatureUuId featureUuId = new FeatureUuId(UuId.fromString(uuid.toString()));

        // 2. Retrieve existing aggregate
        FeaturesAggregate aggregate = repository.findByUuId(featureUuId)
                .orElseThrow(() -> new IllegalArgumentException("Feature not found: " + uuid));

        // 3. Map DTO to Domain Actor context
        Actor actor = FeaturesArchiveMapper.toActor(dto);

        // 4. Invoke Domain Logic (Triggers verifyLifecycleAuthority and records the event)
        aggregate.archive(actor);

        // 5. Persist and return the updated state
        return FeatureDTO.fromAggregate(repository.save(aggregate));
    }
}

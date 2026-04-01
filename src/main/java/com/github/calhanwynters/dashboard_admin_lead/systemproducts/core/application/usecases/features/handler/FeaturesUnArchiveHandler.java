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
 * Handler for unarchiving a Feature.
 * Restores the feature to an active state for business operations.
 */
@Service
public class FeaturesUnArchiveHandler {

    private final FeaturesRepository repository;

    public FeaturesUnArchiveHandler(FeaturesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FeatureDTO handle(UUID uuid, FeaturesArchiveDTO dto) {
        // 1. Map raw UUID to Domain Value Object
        FeatureUuId featureUuId = new FeatureUuId(UuId.fromString(uuid.toString()));

        // 2. Retrieve existing aggregate
        FeaturesAggregate aggregate = repository.findByUuId(featureUuId)
                .orElseThrow(() -> new IllegalArgumentException("Feature not found: " + uuid));

        // 3. Map DTO to Domain Actor context (using the ArchiveMapper)
        Actor actor = FeaturesArchiveMapper.toActor(dto);

        // 4. Invoke Domain Logic
        // Triggers verifyLifecycleAuthority (Manager/Admin required)
        aggregate.unarchive(actor);

        // 5. Persist and return updated state
        return FeatureDTO.fromAggregate(repository.save(aggregate));
    }
}

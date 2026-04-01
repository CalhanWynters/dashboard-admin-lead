package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto.FeatureDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto.FeatureSoftDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.mapper.FeatureSoftMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.FeaturesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Handler for restoring a soft-deleted Feature.
 * Restricts restoration to Administrators per BaseAggregateRoot logic.
 */
@Service
public class FeatureSoftRestoreHandler {

    private final FeaturesRepository repository;

    public FeatureSoftRestoreHandler(FeaturesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FeatureDTO handle(UUID uuid, FeatureSoftDTO dto) {
        // 1. Map raw UUID to Domain Value Object
        FeatureUuId featureUuId = new FeatureUuId(UuId.fromString(uuid.toString()));

        // 2. Retrieve the existing aggregate
        FeaturesAggregate aggregate = repository.findByUuId(featureUuId)
                .orElseThrow(() -> new IllegalArgumentException("Feature not found: " + uuid));

        // 3. Map DTO to Domain Actor context (using SoftMapper since the DTO is identical)
        Actor actor = FeatureSoftMapper.toActor(dto);

        // 4. Invoke Domain Logic
        // Triggers verifyRestorable(actor) which requires ROLE_ADMIN
        aggregate.restore(actor);

        // 5. Persist and return
        return FeatureDTO.fromAggregate(repository.save(aggregate));
    }
}

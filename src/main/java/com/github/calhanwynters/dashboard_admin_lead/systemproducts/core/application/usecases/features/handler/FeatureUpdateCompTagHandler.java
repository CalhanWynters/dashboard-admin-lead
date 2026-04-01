package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto.FeatureDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto.FeatureUpdateCompTagDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.mapper.FeatureUpdateCompTagMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureLabel;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.FeaturesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Handler for updating a Feature's Compatibility Tag.
 * Orchestrates domain-level validation and persistence.
 */
@Service
public class FeatureUpdateCompTagHandler {

    private final FeaturesRepository repository;

    public FeatureUpdateCompTagHandler(FeaturesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FeatureDTO handle(UUID uuid, FeatureUpdateCompTagDTO dto) {
        // 1. Map raw UUID to Domain Technical ID
        FeatureUuId featureUuId = new FeatureUuId(UuId.fromString(uuid.toString()));

        // 2. Retrieve existing aggregate
        FeaturesAggregate aggregate = repository.findByUuId(featureUuId)
                .orElseThrow(() -> new IllegalArgumentException("Feature not found: " + uuid));

        // 3. Map DTO to Domain Types via Mapper
        FeatureLabel newTag = FeatureUpdateCompTagMapper.toDomainLabel(dto);
        Actor actor = FeatureUpdateCompTagMapper.toActor(dto);

        // 4. Invoke Domain Logic
        // Triggers FeaturesBehavior.evaluateCompatibilityTagUpdate via applyDomainChange
        aggregate.updateCompatibilityTag(newTag, actor);

        // 5. Persist and return the updated DTO
        return FeatureDTO.fromAggregate(repository.save(aggregate));
    }
}

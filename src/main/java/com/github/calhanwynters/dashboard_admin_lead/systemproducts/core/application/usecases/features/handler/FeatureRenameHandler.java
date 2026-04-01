package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto.FeatureDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto.FeatureRenameDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.mapper.FeatureRenameMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureName;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.FeaturesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FeatureRenameHandler {

    private final FeaturesRepository repository;

    public FeatureRenameHandler(FeaturesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FeatureDTO handle(UUID uuid, FeatureRenameDTO dto) {
        // 1. Load the Aggregate
        FeatureUuId featureUuId = new FeatureUuId(UuId.fromString(uuid.toString()));
        FeaturesAggregate aggregate = repository.findByUuId(featureUuId)
                .orElseThrow(() -> new IllegalArgumentException("Feature not found: " + uuid));

        // 2. Map DTO to Domain Types using Mapper
        FeatureName newName = FeatureRenameMapper.toDomainName(dto);
        Actor actor = FeatureRenameMapper.toActor(dto);

        // 3. Execute Domain Logic
        // This triggers FeaturesBehavior.evaluateRename via the applyDomainChange engine
        aggregate.rename(newName, actor);

        // 4. Persist and Return
        return FeatureDTO.fromAggregate(repository.save(aggregate));
    }
}

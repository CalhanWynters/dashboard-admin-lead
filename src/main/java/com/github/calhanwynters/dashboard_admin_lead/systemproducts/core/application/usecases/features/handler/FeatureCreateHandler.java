package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.handler;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.command.FeatureCreateCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto.FeatureDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.mapper.FeatureMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.FeaturesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeatureCreateHandler {

    private final FeaturesRepository repository;

    public FeatureCreateHandler(FeaturesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FeatureDTO handle(FeatureCreateCommand command) {
        // 1. Use the Mapper to handle the "Nesting Doll" conversions
        var parts = FeatureMapper.mapToDomain(command);

        // 2. Execute Domain Factory (Includes validation & event registration)
        FeaturesAggregate aggregate = FeaturesAggregate.create(
                parts.uuId(),
                parts.bUuId(),
                parts.name(),
                parts.tag(),
                command.actor()
        );

        // 3. Persist and return via Mapper
        return FeatureMapper.toDTO(repository.save(aggregate));
    }
}

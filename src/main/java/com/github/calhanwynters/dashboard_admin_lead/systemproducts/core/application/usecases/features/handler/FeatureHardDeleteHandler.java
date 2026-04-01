package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.handler;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.command.FeatureHardDeleteCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out.FeaturesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeatureHardDeleteHandler {

    private final FeaturesRepository repository;

    public FeatureHardDeleteHandler(FeaturesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void handle(FeatureHardDeleteCommand command) {
        // 1. Map raw UUID to Domain Value Object
        FeatureUuId featureUuId = new FeatureUuId(UuId.fromString(command.uuid().toString()));

        // 2. Retrieve the existing aggregate (or throw if not found)
        FeaturesAggregate aggregate = repository.findByUuId(featureUuId)
                .orElseThrow(() -> new IllegalArgumentException("Feature not found: " + command.uuid()));

        // 3. Invoke Domain Logic (Triggers SOC 2 checks & registers events)
        // IMPORTANT: This call triggers registerEvent(new FeatureHardDeletedEvent(...))
        aggregate.hardDelete(command.actor());

        // 4. Physical Removal using the defined port method
        // Note: Ensure your Repository Implementation (Adapter) flushes events
        // before the record is physically wiped.
        repository.hardDelete(featureUuId);
    }

}

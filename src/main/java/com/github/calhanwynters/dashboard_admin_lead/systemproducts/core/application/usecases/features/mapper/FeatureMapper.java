package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Label;
import com.github.calhanwynters.dashboard_admin_lead.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.command.FeatureCreateCommand;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto.FeatureDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;

/**
 * Centralized Mapper for the Features sub-domain.
 * Handles the boundary between Application Primitives and Domain Value Objects.
 */
public final class FeatureMapper {

    private FeatureMapper() {} // Static utility only

    /**
     * Map a Create Command to specialized Domain Value Objects.
     */
    public static DomainParts mapToDomain(FeatureCreateCommand command) {
        return new DomainParts(
                new FeatureUuId(UuId.fromString(command.uuid().toString())),
                new FeatureBusinessUuId(UuId.fromString(command.businessUuid())),
                new FeatureName(Name.from(command.name())),
                new FeatureLabel(Label.from(command.compatibilityTag()))
        );
    }

    /**
     * Delegate to the existing Aggregate-to-DTO logic.
     */
    public static FeatureDTO toDTO(FeaturesAggregate aggregate) {
        return FeatureDTO.fromAggregate(aggregate);
    }

    /**
     * Helper record to pass validated domain parts back to the Handler.
     */
    public record DomainParts(
            FeatureUuId uuId,
            FeatureBusinessUuId bUuId,
            FeatureName name,
            FeatureLabel tag
    ) {}
}

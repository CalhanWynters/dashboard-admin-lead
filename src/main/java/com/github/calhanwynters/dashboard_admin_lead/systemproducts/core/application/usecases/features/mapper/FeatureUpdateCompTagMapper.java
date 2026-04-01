package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.Label;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto.FeatureUpdateCompTagDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureLabel;
import java.util.Set;

/**
 * Mapper for Compatibility Tag update operations.
 * Bridges raw label input to the hardened Domain FeatureLabel.
 */
public final class FeatureUpdateCompTagMapper {

    private FeatureUpdateCompTagMapper() {} // Static utility only

    /**
     * Map the Update DTO to a Domain FeatureLabel.
     * Triggers DomainGuard validation for Label syntax and length.
     */
    public static FeatureLabel toDomainLabel(FeatureUpdateCompTagDTO dto) {
        return new FeatureLabel(Label.from(dto.newTag()));
    }

    /**
     * Map the Update DTO to a Domain Actor.
     */
    public static Actor toActor(FeatureUpdateCompTagDTO dto) {
        return Actor.of(
                dto.actorId(),
                dto.actorRoles() != null ? dto.actorRoles() : Set.of()
        );
    }
}

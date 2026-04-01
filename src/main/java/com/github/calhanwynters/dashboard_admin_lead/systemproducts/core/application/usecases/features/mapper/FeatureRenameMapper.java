package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto.FeatureRenameDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureName;
import java.util.Set;

public final class FeatureRenameMapper {

    private FeatureRenameMapper() {}

    public static FeatureName toDomainName(FeatureRenameDTO dto) {
        return new FeatureName(Name.from(dto.newName()));
    }

    public static Actor toActor(FeatureRenameDTO dto) {
        return Actor.of(
                dto.actorId(),
                dto.actorRoles() != null ? dto.actorRoles() : Set.of()
        );
    }
}

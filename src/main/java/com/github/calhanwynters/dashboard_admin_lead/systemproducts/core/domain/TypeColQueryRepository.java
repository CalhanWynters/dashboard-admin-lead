package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
import java.util.Optional;

public interface TypeColQueryRepository {
    Optional<TypeColProjectionDTO> findProjectionByBusinessId(UuId businessId);
}

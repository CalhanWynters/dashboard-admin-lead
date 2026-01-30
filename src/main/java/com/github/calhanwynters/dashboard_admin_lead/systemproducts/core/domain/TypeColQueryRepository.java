package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.type.TypeCollectionAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
import java.util.List;
import java.util.Optional;

/**
 * Read-side repository for TypeCollection.
 * Provides access to the current state of the aggregate for query operations.
 */
public interface TypeColQueryRepository {

    /**
     * Retrieves the current snapshot of a TypeCollection by its unique domain ID.
     */
    Optional<TypeCollectionAggregate> findById(UuId typeColId);

    /**
     * Retrieves all TypeCollection snapshots belonging to a specific business.
     */
    List<TypeCollectionAggregate> findAllByBusinessId(UuId businessId);

    /**
     * Checks if a TypeCollection exists for a specific business.
     */
    boolean existsByBusinessId(UuId businessId);
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist.VariantListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.VariantsUuId;

import java.util.Set;

public class VariantListFactory {

    /**
     * Delegates to the Aggregate's static factory to ensure the
     * VariantListCreatedEvent is captured by the AbstractAggregateRoot.
     */
    public static VariantListAggregate create(VariantListBusinessUuId bizId, Actor creator) {
        // We delegate to the aggregate to ensure the creation event is fired
        return VariantListAggregate.create(
                VariantListUuId.generate(),
                bizId,
                creator
        );
    }

    /**
     * Reconstitutes the aggregate from database state.
     * Includes the 'deleted' flag to restore lifecycle status.
     */
    public static VariantListAggregate reconstitute(
            VariantListId id,
            VariantListUuId uuId,
            VariantListBusinessUuId bizId,
            Set<VariantsUuId> ids,
            boolean deleted, // Added state for restoration
            AuditMetadata audit) {

        return new VariantListAggregate(
                id,
                uuId,
                bizId,
                ids,
                deleted,
                audit
        );
    }
}

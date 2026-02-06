package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;

import java.util.Set;

public class VariantFactory {

    /**
     * Delegates to the Aggregate's static factory to ensure the
     * VariantCreatedEvent is properly registered.
     */
    public static VariantsAggregate create(
            VariantsBusinessUuId bizId,
            VariantsName name,
            Actor creator) {

        return VariantsAggregate.create(
                VariantsUuId.generate(),
                bizId,
                name,
                creator
        );
    }

    /**
     * Rebuilds the aggregate from persistence state.
     * Includes the 'deleted' flag to restore the lifecycle status.
     */
    public static VariantsAggregate reconstitute(
            VariantsId id,
            VariantsUuId uuId,
            VariantsBusinessUuId bizId,
            VariantsName name,
            Set<FeatureUuId> features,
            boolean deleted, // 6th Arg
            AuditMetadata audit) { // 7th Arg

        return new VariantsAggregate(
                id,
                uuId,
                bizId,
                name,
                features,
                deleted,
                audit
        );
    }
}

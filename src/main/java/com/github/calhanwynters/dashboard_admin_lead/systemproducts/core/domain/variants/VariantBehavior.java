package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.FeatureUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.VariantsName;

public class VariantBehavior {

    private final VariantsAggregate variant;

    public VariantBehavior(VariantsAggregate variant) {
        DomainGuard.notNull(variant, "Variant Aggregate instance");
        this.variant = variant;
    }

    public VariantsAggregate rename(VariantsName newName, Actor actor) {
        DomainGuard.notNull(actor, "Actor");
        variant.rename(newName, actor);
        return variant;
    }

    public VariantsAggregate assignFeature(FeatureUuId featureUuId, Actor actor) {
        DomainGuard.notNull(actor, "Actor");
        variant.assignFeature(featureUuId, actor);
        return variant;
    }
}

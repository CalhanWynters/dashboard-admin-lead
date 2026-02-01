package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.VariantsId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.VariantsUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.VariantsBusinessUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.VariantsName;

import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.FeatureUuId;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VariantsAggregate extends AbstractAggregateRoot<VariantsAggregate> {

    private final VariantsId variantsId;
    private final VariantsUuId variantsUuId;
    private final VariantsBusinessUuId variantsBusinessUuId;
    private final VariantsName variantsName;
    private final Set<FeatureUuId> assignedFeatureUuIds; // 1. Added class field

    public VariantsAggregate(VariantsId variantsId,
                             VariantsUuId variantsUuId,
                             VariantsBusinessUuId variantsBusinessUuId,
                             VariantsName variantsName,
                             Set<FeatureUuId> assignedFeatureUuIds) {

        // Validation checks
        DomainGuard.notNull(variantsId, "VariantsAggregate ID");
        DomainGuard.notNull(variantsUuId, "VariantsAggregate UUID");
        DomainGuard.notNull(variantsBusinessUuId, "VariantsAggregate Business UUID");
        DomainGuard.notNull(variantsName, "VariantsAggregate Name");
        DomainGuard.notNull(assignedFeatureUuIds, "Assigned Feature Set");

        this.variantsId = variantsId;
        this.variantsUuId = variantsUuId;
        this.variantsBusinessUuId = variantsBusinessUuId;
        this.variantsName = variantsName;
        // 2. Assigned defensive copy to the class field
        this.assignedFeatureUuIds = new HashSet<>(assignedFeatureUuIds);
    }

    // Getters
    public VariantsId getVariantsId() { return variantsId; }
    public VariantsUuId getVariantsUuId() { return variantsUuId; }
    public VariantsBusinessUuId getVariantsBusinessUuId() { return variantsBusinessUuId; }
    public VariantsName getVariantsName() { return variantsName; }

    // 3. Added getter for the feature set
    public Set<FeatureUuId> getAssignedFeatureUuIds() {
        return Collections.unmodifiableSet(assignedFeatureUuIds);
    }
}

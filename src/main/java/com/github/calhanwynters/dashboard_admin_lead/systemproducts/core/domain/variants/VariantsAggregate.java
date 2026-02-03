package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.FeatureUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VariantsAggregate extends BaseAggregateRoot<VariantsAggregate> {

    private final VariantsId variantsId;
    private final VariantsUuId variantsUuId;
    private final VariantsBusinessUuId variantsBusinessUuId;
    private VariantsName variantsName;
    private final Set<FeatureUuId> assignedFeatureUuIds;

    public VariantsAggregate(VariantsId variantsId,
                             VariantsUuId variantsUuId,
                             VariantsBusinessUuId variantsBusinessUuId,
                             VariantsName variantsName,
                             Set<FeatureUuId> assignedFeatureUuIds,
                             AuditMetadata auditMetadata) {
        super(auditMetadata);

        DomainGuard.notNull(variantsId, "Variant ID");
        DomainGuard.notNull(variantsUuId, "Variant UUID");
        DomainGuard.notNull(variantsBusinessUuId, "Business UUID");
        DomainGuard.notNull(variantsName, "Variant Name");
        DomainGuard.notNull(assignedFeatureUuIds, "Assigned Feature Set");

        this.variantsId = variantsId;
        this.variantsUuId = variantsUuId;
        this.variantsBusinessUuId = variantsBusinessUuId;
        this.variantsName = variantsName;
        this.assignedFeatureUuIds = new HashSet<>(assignedFeatureUuIds);
    }

    /**
     * Bridge for VariantsBehavior audit access.
     */
    void triggerAuditUpdate(Actor actor) {
        this.recordUpdate(actor);
    }

    void updateNameInternal(VariantsName newName) {
        this.variantsName = newName;
    }

    void assignFeatureInternal(FeatureUuId featureUuId) {
        this.assignedFeatureUuIds.add(featureUuId);
    }

    // Getters
    public VariantsId getVariantsId() { return variantsId; }
    public VariantsUuId getVariantsUuId() { return variantsUuId; }
    public VariantsBusinessUuId getVariantsBusinessUuId() { return variantsBusinessUuId; }
    public VariantsName getVariantsName() { return variantsName; }
    public Set<FeatureUuId> getAssignedFeatureUuIds() { return Collections.unmodifiableSet(assignedFeatureUuIds); }

    /**
     * Renames the variant and refreshes the audit trail.
     */
    public void rename(VariantsName newName, Actor actor) {
        DomainGuard.notNull(newName, "New Variant Name");
        DomainGuard.notNull(actor, "Actor");

        this.variantsName = newName;
        this.recordUpdate(actor);
    }

    /**
     * Assigns a feature and refreshes the audit trail if the state changed.
     */
    public void assignFeature(FeatureUuId featureUuId, Actor actor) {
        DomainGuard.notNull(featureUuId, "Feature UUID");
        DomainGuard.notNull(actor, "Actor");

        if (this.assignedFeatureUuIds.add(featureUuId)) {
            this.recordUpdate(actor);
        }
    }

    /**
     * Removes a feature and refreshes the audit trail.
     */
    public void unassignFeature(FeatureUuId featureUuId, Actor actor) {
        DomainGuard.notNull(actor, "Actor");

        if (this.assignedFeatureUuIds.remove(featureUuId)) {
            this.recordUpdate(actor);
        }
    }
}

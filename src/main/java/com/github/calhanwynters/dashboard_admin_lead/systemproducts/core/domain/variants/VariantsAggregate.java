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

/**
 * Aggregate Root for Product Variants.
 * Manages identity, naming, and feature associations with mandatory audit attribution.
 */
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

    // --- DOMAIN ACTIONS ---

    /**
     * Renames the variant, refreshes audit, and triggers rename event.
     */
    public void rename(VariantsName newName, Actor actor) {
        DomainGuard.notNull(newName, "New Variant Name");
        DomainGuard.notNull(actor, "Actor performing the rename");

        this.variantsName = newName;
        this.recordUpdate(actor);

        // Publish the specific rename event
        this.registerEvent(new VariantRenamedEvent(this.variantsUuId, newName, actor));
    }

    /**
     * Assigns a feature and triggers event if state changed.
     */
    public void assignFeature(FeatureUuId featureUuId, Actor actor) {
        DomainGuard.notNull(featureUuId, "Feature UUID to assign");
        DomainGuard.notNull(actor, "Actor");

        if (this.assignedFeatureUuIds.add(featureUuId)) {
            this.recordUpdate(actor);
            this.registerEvent(new FeatureAssignedEvent(this.variantsUuId, featureUuId, actor));
        }
    }

    /**
     * Unassigns a feature and triggers event if state changed.
     */
    public void unassignFeature(FeatureUuId featureUuId, Actor actor) {
        DomainGuard.notNull(featureUuId, "Feature UUID to unassign");
        DomainGuard.notNull(actor, "Actor");

        if (this.assignedFeatureUuIds.remove(featureUuId)) {
            this.recordUpdate(actor);
            this.registerEvent(new FeatureUnassignedEvent(this.variantsUuId, featureUuId, actor));
        }
    }

    /**
     * Marks the variant as soft-deleted and triggers associated event.
     */
    public void softDelete(Actor actor) {
        DomainGuard.notNull(actor, "Actor");
        this.recordUpdate(actor);
        this.registerEvent(new VariantSoftDeletedEvent(this.variantsUuId, actor));
    }

    /**
     * Triggers the hard delete event (usually handled by the repository/service afterwards).
     */
    public void hardDelete(Actor actor) {
        DomainGuard.notNull(actor, "Actor");
        this.registerEvent(new VariantHardDeletedEvent(this.variantsUuId, actor));
    }

    // --- ACCESSORS ---
    public VariantsId getVariantsId() { return variantsId; }
    public VariantsUuId getVariantsUuId() { return variantsUuId; }
    public VariantsBusinessUuId getVariantsBusinessUuId() { return variantsBusinessUuId; }
    public VariantsName getVariantsName() { return variantsName; }

    /**
     * Returns an unmodifiable view of assigned features to maintain aggregate integrity.
     */
    public Set<FeatureUuId> getAssignedFeatureUuIds() {
        return Collections.unmodifiableSet(assignedFeatureUuIds);
    }
}

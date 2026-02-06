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
    private VariantsBusinessUuId variantsBusinessUuId;

    private VariantsName variantsName;
    private final Set<FeatureUuId> assignedFeatureUuIds;
    private boolean deleted;

    public VariantsAggregate(VariantsId variantsId,
                             VariantsUuId variantsUuId,
                             VariantsBusinessUuId variantsBusinessUuId,
                             VariantsName variantsName,
                             Set<FeatureUuId> assignedFeatureUuIds,
                             boolean deleted,
                             AuditMetadata auditMetadata) {
        super(auditMetadata);
        this.variantsId = variantsId;
        this.variantsUuId = DomainGuard.notNull(variantsUuId, "Variant UUID");
        this.variantsBusinessUuId = DomainGuard.notNull(variantsBusinessUuId, "Business UUID");
        this.variantsName = DomainGuard.notNull(variantsName, "Variant Name");
        this.assignedFeatureUuIds = new HashSet<>(assignedFeatureUuIds != null ? assignedFeatureUuIds : Collections.emptySet());
        this.deleted = deleted;
    }

    public static VariantsAggregate create(VariantsUuId uuId, VariantsBusinessUuId bUuId,
                                           VariantsName name, Actor actor) {
        VariantsAggregate aggregate = new VariantsAggregate(
                null, uuId, bUuId, name, new HashSet<>(), false, AuditMetadata.create(actor)
        );
        aggregate.registerEvent(new VariantCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    public void rename(VariantsName newName, Actor actor) {
        VariantsBehavior.ensureActive(this.deleted);
        var validatedName = VariantsBehavior.evaluateRename(this.variantsName, newName);

        this.applyChange(actor,
                new VariantRenamedEvent(this.variantsUuId, validatedName, actor),
                () -> this.variantsName = validatedName
        );
    }

    public void changeBusinessId(VariantsBusinessUuId newId, Actor actor) {
        VariantsBehavior.ensureActive(this.deleted);
        var oldId = this.variantsBusinessUuId;
        var validatedId = VariantsBehavior.evaluateBusinessIdChange(oldId, newId);

        this.applyChange(actor,
                new VariantBusinessUuIdChangedEvent(this.variantsUuId, oldId, validatedId, actor),
                () -> this.variantsBusinessUuId = validatedId
        );
    }

    public void assignFeature(FeatureUuId featureUuId, Actor actor) {
        VariantsBehavior.ensureActive(this.deleted);
        VariantsBehavior.ensureCanAssign(this.assignedFeatureUuIds, featureUuId);

        this.applyChange(actor,
                new FeatureAssignedEvent(this.variantsUuId, featureUuId, actor),
                () -> this.assignedFeatureUuIds.add(featureUuId)
        );
    }

    public void unassignFeature(FeatureUuId featureUuId, Actor actor) {
        VariantsBehavior.ensureActive(this.deleted);
        VariantsBehavior.ensureCanUnassign(this.assignedFeatureUuIds, featureUuId);

        this.applyChange(actor,
                new FeatureUnassignedEvent(this.variantsUuId, featureUuId, actor),
                () -> this.assignedFeatureUuIds.remove(featureUuId)
        );
    }

    public void unassignAllFeatures(Actor actor) {
        VariantsBehavior.ensureActive(this.deleted);
        if (this.assignedFeatureUuIds.isEmpty()) return;

        this.applyChange(actor,
                new AllFeaturesUnassignedEvent(this.variantsUuId, actor),
                this.assignedFeatureUuIds::clear
        );
    }

    public void requestUsageAudit(Actor actor) {
        this.applyChange(actor, new VariantUsageAuditRequestedEvent(this.variantsUuId, actor), null);
    }

    public void softDelete(Actor actor) {
        VariantsBehavior.ensureActive(this.deleted);
        this.applyChange(actor, new VariantSoftDeletedEvent(this.variantsUuId, actor), () -> this.deleted = true);
    }

    public void restore(Actor actor) {
        if (!this.deleted) return;
        this.applyChange(actor, new VariantRestoredEvent(this.variantsUuId, actor), () -> this.deleted = false);
    }

    public void hardDelete(Actor actor) {
        this.applyChange(actor, new VariantHardDeletedEvent(this.variantsUuId, actor), null);
    }

    // --- ACCESSORS ---
    public boolean isDeleted() { return deleted; }
    public VariantsId getVariantsId() { return variantsId; }
    public VariantsUuId getVariantsUuId() { return variantsUuId; }
    public VariantsBusinessUuId getVariantsBusinessUuId() { return variantsBusinessUuId; }
    public VariantsName getVariantsName() { return variantsName; }
    public Set<FeatureUuId> getAssignedFeatureUuIds() { return Collections.unmodifiableSet(assignedFeatureUuIds); }
}

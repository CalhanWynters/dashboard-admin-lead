package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.events.*;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VariantsAggregate extends BaseAggregateRoot<VariantsAggregate> {

    private final VariantsId variantsId;
    private final VariantsUuId variantsUuId;
    private VariantsBusinessUuId variantsBusinessUuId;
    private VariantsName variantsName;
    private final Set<FeatureUuId> assignedFeatureUuIds;
    private ProductBooleans productBooleans;

    public VariantsAggregate(VariantsId variantsId,
                             VariantsUuId variantsUuId,
                             VariantsBusinessUuId variantsBusinessUuId,
                             VariantsName variantsName,
                             Set<FeatureUuId> assignedFeatureUuIds,
                             ProductBooleans productBooleans, // 2. Updated param
                             AuditMetadata auditMetadata) {
        super(auditMetadata);
        this.variantsId = variantsId;
        this.variantsUuId = DomainGuard.notNull(variantsUuId, "Variant UUID");
        this.variantsBusinessUuId = DomainGuard.notNull(variantsBusinessUuId, "Business UUID");
        this.variantsName = DomainGuard.notNull(variantsName, "Variant Name");
        this.assignedFeatureUuIds = new HashSet<>(assignedFeatureUuIds != null ? assignedFeatureUuIds : Collections.emptySet());
        // 3. Ensure productBooleans is never null
        this.productBooleans = productBooleans != null ? productBooleans : new ProductBooleans(false, false);
    }

    public static VariantsAggregate create(VariantsUuId uuId, VariantsBusinessUuId bUuId,
                                           VariantsName name, Actor actor) {
        VariantsBehavior.verifyCreationAuthority(actor);

        // 4. Initialize with default record state
        VariantsAggregate aggregate = new VariantsAggregate(
                null, uuId, bUuId, name, new HashSet<>(), new ProductBooleans(false, false), AuditMetadata.create(actor)
        );
        aggregate.registerEvent(new VariantCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    public void rename(VariantsName newName, Actor actor) {
        VariantsBehavior.ensureActive(this.productBooleans.softDeleted()); // 5. Use record accessor
        var validatedName = VariantsBehavior.evaluateRename(this.variantsName, newName, actor);

        this.applyChange(actor,
                new VariantRenamedEvent(this.variantsUuId, validatedName, actor),
                () -> this.variantsName = validatedName
        );
    }

    public void changeBusinessId(VariantsBusinessUuId newId, Actor actor) {
        VariantsBehavior.ensureActive(this.productBooleans.softDeleted());
        var oldId = this.variantsBusinessUuId;
        var validatedId = VariantsBehavior.evaluateBusinessIdChange(oldId, newId, actor);

        this.applyChange(actor,
                new VariantBusinessUuIdChangedEvent(this.variantsUuId, oldId, validatedId, actor),
                () -> this.variantsBusinessUuId = validatedId
        );
    }

    public void assignFeature(FeatureUuId featureUuId, Actor actor) {
        VariantsBehavior.ensureActive(this.productBooleans.softDeleted());
        VariantsBehavior.ensureCanAssign(this.assignedFeatureUuIds, featureUuId, actor);

        this.applyChange(actor,
                new FeatureAssignedEvent(this.variantsUuId, featureUuId, actor),
                () -> this.assignedFeatureUuIds.add(featureUuId)
        );
    }

    public void unassignFeature(FeatureUuId featureUuId, Actor actor) {
        VariantsBehavior.ensureActive(this.productBooleans.softDeleted());
        VariantsBehavior.ensureCanUnassign(this.assignedFeatureUuIds, featureUuId, actor);

        this.applyChange(actor,
                new FeatureUnassignedEvent(this.variantsUuId, featureUuId, actor),
                () -> this.assignedFeatureUuIds.remove(featureUuId)
        );
    }

    public void unassignAllFeatures(Actor actor) {
        VariantsBehavior.ensureActive(this.productBooleans.softDeleted());
        VariantsBehavior.verifyManagementAuthority(actor);

        if (this.assignedFeatureUuIds.isEmpty()) return;

        this.applyChange(actor,
                new AllFeaturesUnassignedEvent(this.variantsUuId, actor),
                this.assignedFeatureUuIds::clear
        );
    }

    public void requestUsageAudit(Actor actor) {
        // No modification to state, but we authorize the request
        VariantsBehavior.verifyManagementAuthority(actor);
        this.applyChange(actor, new VariantUsageAuditRequestedEvent(this.variantsUuId, actor), null);
    }

    public void archive(Actor actor) {
        // Line 1: Auth & Logic
        VariantsBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record to set archived = true)
        this.applyChange(actor,
                new VariantArchivedEvent(this.variantsUuId, actor),
                () -> this.productBooleans = new ProductBooleans(true, this.productBooleans.softDeleted())
        );
    }

    public void unarchive(Actor actor) {
        // Line 1: Auth & Logic
        VariantsBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record to set archived = false)
        this.applyChange(actor,
                new VariantUnarchivedEvent(this.variantsUuId, actor),
                () -> this.productBooleans = new ProductBooleans(false, this.productBooleans.softDeleted())
        );
    }

    public void softDelete(Actor actor) {
        VariantsBehavior.ensureActive(this.productBooleans.softDeleted());
        VariantsBehavior.verifyLifecycleAuthority(actor);

        // 6. Replace the entire record instance to change state
        this.applyChange(actor,
                new VariantSoftDeletedEvent(this.variantsUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), true)
        );
    }

    public void restore(Actor actor) {
        if (!this.productBooleans.softDeleted()) return;
        VariantsBehavior.verifyLifecycleAuthority(actor);

        // 7. Reset softDeleted to false
        this.applyChange(actor,
                new VariantRestoredEvent(this.variantsUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), false)
        );
    }

    public void hardDelete(Actor actor) {
        VariantsBehavior.verifyLifecycleAuthority(actor);
        this.applyChange(actor, new VariantHardDeletedEvent(this.variantsUuId, actor), null);
    }

    // --- ACCESSORS ---
    public ProductBooleans getProductBooleans() { return productBooleans; }
    public boolean isDeleted() { return productBooleans.softDeleted(); }
    public VariantsId getVariantsId() { return variantsId; }
    public VariantsUuId getVariantsUuId() { return variantsUuId; }
    public VariantsBusinessUuId getVariantsBusinessUuId() { return variantsBusinessUuId; }
    public VariantsName getVariantsName() { return variantsName; }
    public Set<FeatureUuId> getAssignedFeatureUuIds() { return Collections.unmodifiableSet(assignedFeatureUuIds); }
}

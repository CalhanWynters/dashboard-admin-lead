package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.LEGACYBaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleansLEGACY;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.events.*;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;

public class FeaturesAggregateLEGACY extends LEGACYBaseAggregateRoot<FeaturesAggregateLEGACY> {

    private final FeatureId featuresId;
    private final FeatureUuId featuresUuId;

    private FeatureBusinessUuId featuresBusinessUuId;
    private FeatureName featuresName;
    private FeatureLabel compatibilityTag;
    private ProductBooleansLEGACY productBooleansLEGACY; // Record integration
    // Add Version-Based Optimistic Locking "optLockVer"
    // Add Schema-Based Versioning "schemaVer"

    public FeaturesAggregateLEGACY(FeatureId featuresId,
                                   FeatureUuId featuresUuId,
                                   FeatureBusinessUuId featuresBusinessUuId,
                                   FeatureName featuresName,
                                   FeatureLabel compatibilityTag,
                                   ProductBooleansLEGACY productBooleansLEGACY, // Added param
                                   AuditMetadata auditMetadata) {

        super(auditMetadata);
        this.featuresId = DomainGuard.notNull(featuresId, "Feature PK ID");
        this.featuresUuId = DomainGuard.notNull(featuresUuId, "Feature UUID");
        this.featuresBusinessUuId = DomainGuard.notNull(featuresBusinessUuId, "Feature Business UUID");
        this.featuresName = DomainGuard.notNull(featuresName, "Feature Name");
        this.compatibilityTag = DomainGuard.notNull(compatibilityTag, "Compatibility Tag");
        this.productBooleansLEGACY = (productBooleansLEGACY != null) ? productBooleansLEGACY : new ProductBooleansLEGACY(false, false);
    }

    public static FeaturesAggregateLEGACY create(FeatureUuId uuId, FeatureBusinessUuId bUuId,
                                                 FeatureName name, FeatureLabel tag, Actor actor) {
        FeaturesBehavior.validateCreation(uuId, bUuId, name, tag, actor);

        FeaturesAggregateLEGACY aggregate = new FeaturesAggregateLEGACY(
                null, uuId, bUuId, name, tag, new ProductBooleansLEGACY(false, false), AuditMetadata.create(actor)
        );
        aggregate.registerEvent(new FeatureCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---


    public void syncToKafka(Actor actor) {
        FeaturesBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        FeaturesBehavior.verifySyncAuthority(actor);

        this.applyChange(actor,
                new FeatureDataSyncedEvent(featuresUuId, featuresBusinessUuId, featuresName, compatibilityTag, productBooleansLEGACY, actor),
                null);
    }

    public void updateBusinessUuId(FeatureBusinessUuId newId, Actor actor) {
        FeaturesBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());

        // Validate using your existing logic (Admin-only, non-null, difference check)
        var validatedId = FeaturesBehavior.evaluateBusinessIdChange(this.featuresBusinessUuId, newId, actor);

        this.applyChange(actor,
                new FeatureBusinessUuIdChangedEvent(featuresUuId, this.featuresBusinessUuId, validatedId, actor),
                () -> this.featuresBusinessUuId = validatedId);
    }

    public void rename(FeatureName newName, Actor actor) {
        FeaturesBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());

        var validatedName = FeaturesBehavior.evaluateRename(this.featuresName, newName, actor);

        this.applyChange(actor,
                new FeatureNameUpdatedEvent(featuresUuId, validatedName, actor),
                () -> this.featuresName = validatedName);
    }

    public void updateCompatibilityTag(FeatureLabel newTag, Actor actor) {
        FeaturesBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());

        var validatedTag = FeaturesBehavior.evaluateCompatibilityTagUpdate(newTag, this.compatibilityTag, actor);

        this.applyChange(actor,
                new FeatureCompTagUpdatedEvent(featuresUuId, this.compatibilityTag, validatedTag, actor),
                () -> this.compatibilityTag = validatedTag);
    }


    public void archive(Actor actor) {
        FeaturesBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new FeatureArchivedEvent(featuresUuId, actor),
                () -> this.productBooleansLEGACY = new ProductBooleansLEGACY(true, this.productBooleansLEGACY.softDeleted())
        );
    }

    public void unarchive(Actor actor) {
        FeaturesBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new FeatureUnarchivedEvent(featuresUuId, actor),
                () -> this.productBooleansLEGACY = new ProductBooleansLEGACY(false, this.productBooleansLEGACY.softDeleted())
        );
    }

    public void softDelete(Actor actor) {
        FeaturesBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        FeaturesBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor, new FeatureSoftDeletedEvent(featuresUuId, actor),
                () -> this.productBooleansLEGACY = new ProductBooleansLEGACY(this.productBooleansLEGACY.archived(), true)
        );
    }

    public void restore(Actor actor) {
        if (!this.productBooleansLEGACY.softDeleted()) return;
        FeaturesBehavior.verifyRestorable(actor);

        this.applyChange(actor, new FeatureRestoredEvent(featuresUuId, actor),
                () -> this.productBooleansLEGACY = new ProductBooleansLEGACY(this.productBooleansLEGACY.archived(), false)
        );
    }

    public void hardDelete(Actor actor) {
        FeaturesBehavior.verifyHardDeleteAuthority(actor);
        this.applyChange(actor, new FeatureHardDeletedEvent(featuresUuId, actor), null);
    }

    // --- GETTERS ---
    public boolean isDeleted() { return productBooleansLEGACY.softDeleted(); }
    public boolean isArchived() { return productBooleansLEGACY.archived(); }
    public ProductBooleansLEGACY getProductBooleans() { return productBooleansLEGACY; }
    public FeatureId getFeaturesId() { return featuresId; }
    public FeatureUuId getFeaturesUuId() { return featuresUuId; }
    public FeatureBusinessUuId getFeaturesBusinessUuId() { return featuresBusinessUuId; }
    public FeatureName getFeaturesName() { return featuresName; }
    public FeatureLabel getCompatibilityTag() { return compatibilityTag; }
}

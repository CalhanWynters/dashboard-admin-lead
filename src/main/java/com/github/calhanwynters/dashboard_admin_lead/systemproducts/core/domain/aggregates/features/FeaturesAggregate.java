package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.events.*;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;

public class FeaturesAggregate extends BaseAggregateRoot<FeaturesAggregate> {

    private final FeatureId featuresId;
    private final FeatureUuId featuresUuId;

    private FeatureBusinessUuId featuresBusinessUuId;
    private FeatureName featuresName;
    private FeatureLabel compatibilityTag;
    private ProductBooleans productBooleans; // Record integration

    public FeaturesAggregate(FeatureId featuresId,
                             FeatureUuId featuresUuId,
                             FeatureBusinessUuId featuresBusinessUuId,
                             FeatureName featuresName,
                             FeatureLabel compatibilityTag,
                             ProductBooleans productBooleans, // Added param
                             AuditMetadata auditMetadata) {

        super(auditMetadata);
        this.featuresId = DomainGuard.notNull(featuresId, "Feature PK ID");
        this.featuresUuId = DomainGuard.notNull(featuresUuId, "Feature UUID");
        this.featuresBusinessUuId = DomainGuard.notNull(featuresBusinessUuId, "Feature Business UUID");
        this.featuresName = DomainGuard.notNull(featuresName, "Feature Name");
        this.compatibilityTag = DomainGuard.notNull(compatibilityTag, "Compatibility Tag");
        this.productBooleans = (productBooleans != null) ? productBooleans : new ProductBooleans(false, false);
    }

    public static FeaturesAggregate create(FeatureUuId uuId, FeatureBusinessUuId bUuId,
                                           FeatureName name, FeatureLabel tag, Actor actor) {
        FeaturesBehavior.validateCreation(uuId, bUuId, name, tag, actor);

        FeaturesAggregate aggregate = new FeaturesAggregate(
                null, uuId, bUuId, name, tag, new ProductBooleans(false, false), AuditMetadata.create(actor)
        );
        aggregate.registerEvent(new FeatureCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    public void syncToKafka(Actor actor) {
        FeaturesBehavior.ensureActive(this.productBooleans.softDeleted());
        FeaturesBehavior.verifySyncAuthority(actor);

        this.applyChange(actor,
                new FeatureDataSyncedEvent(featuresUuId, featuresBusinessUuId, featuresName, compatibilityTag, productBooleans, actor),
                null);
    }

    public void updateBusinessUuId(FeatureBusinessUuId newId, Actor actor) {
        FeaturesBehavior.ensureActive(this.productBooleans.softDeleted());

        // Validate using your existing logic (Admin-only, non-null, difference check)
        var validatedId = FeaturesBehavior.evaluateBusinessIdChange(this.featuresBusinessUuId, newId, actor);

        this.applyChange(actor,
                new FeatureBusinessUuIdChangedEvent(featuresUuId, this.featuresBusinessUuId, validatedId, actor),
                () -> this.featuresBusinessUuId = validatedId);
    }

    public void changeCompatibilityTag(FeatureLabel newTag, Actor actor) {
        FeaturesBehavior.ensureActive(this.productBooleans.softDeleted());
        var validatedTag = FeaturesBehavior.evaluateCompatibilityChange(newTag, this.compatibilityTag, actor);

        this.applyChange(actor,
                new FeatureCompatibilityChangedEvent(featuresUuId, this.compatibilityTag, validatedTag, actor),
                () -> this.compatibilityTag = validatedTag
        );
    }

    public void updateFeatureName(FeatureName newName, Actor actor) {
        FeaturesBehavior.ensureActive(this.productBooleans.softDeleted());

        var validatedName = FeaturesBehavior.evaluateFeatureNameUpdate(newName, actor);

        this.applyChange(actor,
                new FeatureNameUpdatedEvent(featuresUuId, validatedName, actor),
                () -> this.featuresName = validatedName);
    }

    public void updateCompatibilityTag(FeatureLabel newTag, Actor actor) {
        FeaturesBehavior.ensureActive(this.productBooleans.softDeleted());

        var validatedTag = FeaturesBehavior.evaluateCompatibilityTagUpdate(newTag, actor);

        this.applyChange(actor,
                new FeatureCompTagUpdatedEvent(featuresUuId, validatedTag, actor),
                () -> this.compatibilityTag = validatedTag);
    }


    public void archive(Actor actor) {
        FeaturesBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new FeatureArchivedEvent(featuresUuId, actor),
                () -> this.productBooleans = new ProductBooleans(true, this.productBooleans.softDeleted())
        );
    }

    public void unarchive(Actor actor) {
        FeaturesBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new FeatureUnarchivedEvent(featuresUuId, actor),
                () -> this.productBooleans = new ProductBooleans(false, this.productBooleans.softDeleted())
        );
    }

    public void softDelete(Actor actor) {
        FeaturesBehavior.ensureActive(this.productBooleans.softDeleted());
        FeaturesBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor, new FeatureSoftDeletedEvent(featuresUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), true)
        );
    }

    public void restore(Actor actor) {
        if (!this.productBooleans.softDeleted()) return;
        FeaturesBehavior.verifyRestorable(actor);

        this.applyChange(actor, new FeatureRestoredEvent(featuresUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), false)
        );
    }

    public void hardDelete(Actor actor) {
        FeaturesBehavior.verifyHardDeleteAuthority(actor);
        this.applyChange(actor, new FeatureHardDeletedEvent(featuresUuId, actor), null);
    }

    // --- GETTERS ---
    public boolean isDeleted() { return productBooleans.softDeleted(); }
    public boolean isArchived() { return productBooleans.archived(); }
    public ProductBooleans getProductBooleans() { return productBooleans; }
    public FeatureId getFeaturesId() { return featuresId; }
    public FeatureUuId getFeaturesUuId() { return featuresUuId; }
    public FeatureBusinessUuId getFeaturesBusinessUuId() { return featuresBusinessUuId; }
    public FeatureName getFeaturesName() { return featuresName; }
    public FeatureLabel getCompatibilityTag() { return compatibilityTag; }
}

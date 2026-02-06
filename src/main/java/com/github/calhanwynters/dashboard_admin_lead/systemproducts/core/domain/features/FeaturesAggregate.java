package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.events.*;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.*;

/**
 * Aggregate Root for Product Features.
 * Standardized via Rich Domain Model pattern to ensure atomic updates and auditing.
 */
public class FeaturesAggregate extends BaseAggregateRoot<FeaturesAggregate> {

    private final FeatureId featuresId;
    private final FeatureUuId featuresUuId;

    // Removed final to allow the changeBusinessId domain action
    private FeatureBusinessUuId featuresBusinessUuId;
    private FeatureName featuresName;
    private FeatureLabel compatibilityTag;

    /**
     * Standard constructor for reconstitution.
     */
    public FeaturesAggregate(FeatureId featuresId,
                             FeatureUuId featuresUuId,
                             FeatureBusinessUuId featuresBusinessUuId,
                             FeatureName featuresName,
                             FeatureLabel compatibilityTag,
                             AuditMetadata auditMetadata) {

        super(auditMetadata);
        this.featuresId = DomainGuard.notNull(featuresId, "Feature PK ID");
        this.featuresUuId = DomainGuard.notNull(featuresUuId, "Feature UUID");
        this.featuresBusinessUuId = DomainGuard.notNull(featuresBusinessUuId, "Feature Business UUID");
        this.featuresName = DomainGuard.notNull(featuresName, "Feature Name");
        this.compatibilityTag = DomainGuard.notNull(compatibilityTag, "Compatibility Tag");
    }

    /**
     * Static Factory for creating new Features.
     */
    public static FeaturesAggregate create(FeatureUuId uuId, FeatureBusinessUuId bUuId,
                                           FeatureName name, FeatureLabel tag, Actor actor) {
        FeaturesBehavior.validateCreation(uuId, bUuId, name, tag);

        FeaturesAggregate aggregate = new FeaturesAggregate(null, uuId, bUuId, name, tag, AuditMetadata.create(actor));
        aggregate.registerEvent(new FeatureCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS (Two-Liner Pattern) ---

    public void changeCompatibilityTag(FeatureLabel newTag, Actor actor) {
        // Line 1: Pure Logic (Behavior)
        var validatedTag = FeaturesBehavior.evaluateCompatibilityChange(newTag, this.compatibilityTag);

        // Line 2: Side-Effect Execution (BaseAggregateRoot helper)
        this.applyChange(actor,
                new FeatureCompatibilityChangedEvent(featuresUuId, this.compatibilityTag, validatedTag, actor),
                () -> this.compatibilityTag = validatedTag
        );
    }

    public void updateDetails(FeatureName newName, FeatureLabel newTag, Actor actor) {
        var patch = FeaturesBehavior.evaluateUpdate(newName, newTag);

        this.applyChange(actor, new FeatureDetailsUpdatedEvent(featuresUuId, newName, newTag, actor), () -> {
            this.featuresName = patch.name();
            this.compatibilityTag = patch.tag();
        });
    }

    public void changeBusinessId(FeatureBusinessUuId newId, Actor actor) {
        var validatedId = FeaturesBehavior.evaluateBusinessIdChange(this.featuresBusinessUuId, newId);

        this.applyChange(actor, new FeatureBusinessUuIdChangedEvent(featuresUuId, featuresBusinessUuId, validatedId, actor), () -> this.featuresBusinessUuId = validatedId);
    }

    public void softDelete(Actor actor) {
        FeaturesBehavior.verifyDeletable();

        this.applyChange(actor, new FeatureSoftDeletedEvent(featuresUuId, actor), () -> {
            // State mutation for soft delete (e.g., this.active = false) would go here
        });
    }

    public void hardDelete(Actor actor) {
        // Hard delete usually destroys the record, but we fire the event for cleanup listeners
        this.applyChange(actor, new FeatureHardDeletedEvent(featuresUuId, actor), null);
    }

    public void restore(Actor actor) {
        FeaturesBehavior.verifyRestorable();

        this.applyChange(actor, new FeatureRestoredEvent(featuresUuId, actor), () -> {
            // State mutation for restore (e.g., this.active = true) would go here
        });
    }

    // --- GETTERS ---
    public FeatureId getFeaturesId() { return featuresId; }
    public FeatureUuId getFeaturesUuId() { return featuresUuId; }
    public FeatureBusinessUuId getFeaturesBusinessUuId() { return featuresBusinessUuId; }
    public FeatureName getFeaturesName() { return featuresName; }
    public FeatureLabel getCompatibilityTag() { return compatibilityTag; }
}

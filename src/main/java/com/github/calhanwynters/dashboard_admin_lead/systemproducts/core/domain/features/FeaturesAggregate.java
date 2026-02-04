package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.*;

/**
 * Aggregate Root for Product Features.
 * Standardized via Rich Domain Model pattern to ensure atomic updates and auditing.
 */
public class FeaturesAggregate extends BaseAggregateRoot<FeaturesAggregate> {

    private final FeatureId featuresId;
    private final FeatureUuId featuresUuId;
    private final FeatureBusinessUuId featuresBusinessUuId;

    private FeatureName featuresName;
    private FeatureLabel compatibilityTag;

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

    public void updateDetails(FeatureName newName, FeatureLabel newTag, Actor actor) {
        DomainGuard.notNull(newName, "New Feature Name");
        DomainGuard.notNull(newTag, "New Compatibility Tag");
        DomainGuard.notNull(actor, "Actor performing the update");

        this.featuresName = newName;
        this.compatibilityTag = newTag;

        this.recordUpdate(actor);
        this.registerEvent(new FeatureDetailsUpdatedEvent(this.featuresUuId, newName, newTag, actor));
    }

    public void softDelete(Actor actor) {
        DomainGuard.notNull(actor, "Actor");
        this.recordUpdate(actor);
        this.registerEvent(new FeatureSoftDeletedEvent(this.featuresUuId, actor));
    }

    public void hardDelete(Actor actor) {
        DomainGuard.notNull(actor, "Actor");
        this.registerEvent(new FeatureHardDeletedEvent(this.featuresUuId, actor));
    }

    // Getters
    public FeatureId getFeaturesId() { return featuresId; }
    public FeatureUuId getFeaturesUuId() { return featuresUuId; }
    public FeatureBusinessUuId getFeaturesBusinessUuId() { return featuresBusinessUuId; }
    public FeatureName getFeaturesName() { return featuresName; }
    public FeatureLabel getCompatibilityTag() { return compatibilityTag; }
}

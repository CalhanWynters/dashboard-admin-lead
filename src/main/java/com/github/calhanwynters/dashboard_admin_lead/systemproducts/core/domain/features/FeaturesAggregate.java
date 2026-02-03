package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.*;

/**
 * Aggregate Root for Product Features.
 * Inherits standardized business auditing via BaseAggregateRoot.
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

        DomainGuard.notNull(featuresId, "Feature PK ID");
        DomainGuard.notNull(featuresUuId, "Feature UUID");
        DomainGuard.notNull(featuresBusinessUuId, "Feature Business UUID");
        DomainGuard.notNull(featuresName, "Feature Name");
        DomainGuard.notNull(compatibilityTag, "Compatibility Tag");

        this.featuresId = featuresId;
        this.featuresUuId = featuresUuId;
        this.featuresBusinessUuId = featuresBusinessUuId;
        this.featuresName = featuresName;
        this.compatibilityTag = compatibilityTag;
    }

    public void updateDetails(FeatureName newName, FeatureLabel newTag) {
        this.featuresName = newName;
        this.compatibilityTag = newTag;
    }

    public void recordUpdate(Actor actor) {
        super.recordUpdate(actor);
    }

    // Getters
    public FeatureId getFeaturesId() { return featuresId; }
    public FeatureUuId getFeaturesUuId() { return featuresUuId; }
    public FeatureBusinessUuId getFeaturesBusinessUuId() { return featuresBusinessUuId; }
    public FeatureName getFeaturesName() { return featuresName; }
    public FeatureLabel getCompatibilityTag() { return compatibilityTag; }

    public void updateDetails(FeatureName newName, FeatureLabel newTag, Actor actor) {
        DomainGuard.notNull(newName, "New Feature Name");
        DomainGuard.notNull(newTag, "New Compatibility Tag");

        this.featuresName = newName;
        this.compatibilityTag = newTag;

        // Audit is now part of the atomic domain action
        this.recordUpdate(actor);
    }
}

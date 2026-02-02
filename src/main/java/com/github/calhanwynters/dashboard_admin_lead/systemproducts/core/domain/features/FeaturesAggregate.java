package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

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
    private final FeatureName featuresName;
    private final FeatureLabel compatibilityTag;

    public FeaturesAggregate(FeatureId featuresId,
                             FeatureUuId featuresUuId,
                             FeatureBusinessUuId featuresBusinessUuId,
                             FeatureName featuresName,
                             FeatureLabel compatibilityTag,
                             AuditMetadata auditMetadata) { // Mandatory Audit

        super(auditMetadata); // Handles temporal invariants

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

    // Getters
    public FeatureId getFeaturesId() { return featuresId; }
    public FeatureUuId getFeaturesUuId() { return featuresUuId; }
    public FeatureBusinessUuId getFeaturesBusinessUuId() { return featuresBusinessUuId; }
    public FeatureName getFeaturesName() { return featuresName; }
    public FeatureLabel getCompatibilityTag() { return compatibilityTag; }
}

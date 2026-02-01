package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.FeatureId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.FeatureUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.FeatureBusinessUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.FeatureName;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.FeatureLabel;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import org.springframework.data.domain.AbstractAggregateRoot;

public class FeaturesAggregate extends AbstractAggregateRoot<FeaturesAggregate> {

    private final FeatureId featuresId;
    private final FeatureUuId featuresUuId;
    private final FeatureBusinessUuId featuresBusinessUuId;
    private final FeatureName featuresName;
    private final FeatureLabel compatibilityTag;

    // Constructor
    public FeaturesAggregate(FeatureId featuresId,
                             FeatureUuId featuresUuId,
                             FeatureBusinessUuId featuresBusinessUuId,
                             FeatureName featuresName,
                             FeatureLabel compatibilityTag) {
        // Validation checks
        DomainGuard.notNull(featuresId, "Feature PK ID");
        DomainGuard.notNull(featuresUuId, "Feature UUID");
        DomainGuard.notNull(featuresBusinessUuId, "Feature Business UUID");
        DomainGuard.notNull(featuresName, "Feature featuresName");
        DomainGuard.notNull(compatibilityTag, "Compatibility Tag");

        this.featuresId = featuresId;
        this.featuresUuId = featuresUuId;
        this.featuresBusinessUuId = featuresBusinessUuId;
        this.featuresName = featuresName;
        this.compatibilityTag = compatibilityTag;
    }

    // Getters
    public FeatureId getFeaturesId() {
        return featuresId;
    }
    public FeatureUuId getFeaturesUuId() {
        return featuresUuId;
    }
    public FeatureBusinessUuId getFeaturesBusinessUuId() {
        return featuresBusinessUuId;
    }
    public FeatureName getFeaturesName() {
        return featuresName;
    }
    public FeatureLabel getCompatibilityTag() {
        return compatibilityTag;
    }

    // Optionally, you can override hashCode, equals, and toString for better usability
}

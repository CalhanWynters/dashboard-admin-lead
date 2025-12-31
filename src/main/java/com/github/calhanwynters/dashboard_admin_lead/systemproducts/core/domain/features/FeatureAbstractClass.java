package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;


import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;

import java.util.Objects;

public abstract class FeatureAbstractClass {
    private final PkIdVO featureId;
    private final UuIdVO featureUuId;
    private final NameVO featureName;
    private final LabelVO featureLabel;
    private final DescriptionVO featureDescription;
    private final StatusEnums featureStatus;
    private final VersionVO featureVersion;
    private final LastModifiedVO lastModified;
    private final Boolean isUnique;

    protected FeatureAbstractClass(
            PkIdVO featureId,
            UuIdVO featureUuId,
            NameVO featureName,
            LabelVO featureLabel,
            DescriptionVO featureDescription,
            StatusEnums featureStatus,
            VersionVO featureVersion,
            LastModifiedVO lastModified,
            Boolean isUnique) {
        this.featureId = Objects.requireNonNull(featureId, "Feature ID must not be null");
        this.featureUuId = Objects.requireNonNull(featureUuId, "Feature UUID must not be null");
        this.featureName = Objects.requireNonNull(featureName, "Feature Name must not be null");
        this.featureLabel = Objects.requireNonNull(featureLabel, "Feature Label must not be null");
        this.featureDescription = Objects.requireNonNull(featureDescription, "Feature Description must not be null");
        this.featureStatus = Objects.requireNonNull(featureStatus, "Feature Status must not be null");
        this.featureVersion = Objects.requireNonNull(featureVersion, "Feature Version must not be null");
        this.lastModified = Objects.requireNonNull(lastModified, "Last Modified indicator must not be null"); // A boolean value to turn true if feature is unique for use on only 1 variant.
        this.isUnique = Objects.requireNonNull(isUnique, "isUnique flag must not be null");
    }

    public PkIdVO getFeatureId() {return featureId;}
    public UuIdVO getFeatureUuId() {return featureUuId;}
    public NameVO getFeatureName() {return featureName;}
    public LabelVO getFeatureLabel() {return featureLabel;}
    public DescriptionVO getFeatureDescription() {return featureDescription;}
    public StatusEnums getFeatureStatus() {return featureStatus;}
    public VersionVO getFeatureVersion() {return featureVersion;}
    public LastModifiedVO getLastModified() {return lastModified;}
    public Boolean getIsUnique() {return isUnique;}

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeatureAbstractClass that)) return false;
        return Objects.equals(getFeatureUuId(), that.getFeatureUuId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getFeatureUuId());
    }

}

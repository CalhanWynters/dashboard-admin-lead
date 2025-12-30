package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;


/*
 * IdVO featureId
 * UuIdVO featureUuId
 * NameVO featureName
 * LabelVO featureLabel
 * DescriptionVO featureDescription
 * StatusEnums featureStatus
 * VersionVO featureVersion
 * LastModifiedVO lastModified
 * Boolean isUnique
 */


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
        this.lastModified = Objects.requireNonNull(lastModified); // A boolean value to turn true if feature is unique for use on only 1 variant.
        this.isUnique = isUnique;
    }

    public PkIdVO getFeatureId() {return featureId;}
    public UuIdVO getFeatureUuId() {return featureUuId;}
    public NameVO featureName() {return featureName;}
    public LabelVO featureLabel() {return featureLabel;}
    public DescriptionVO featureDescription() {return featureDescription;}
    public StatusEnums featureStatus() {return featureStatus;}
    public VersionVO featureVersion() {return featureVersion;}
    public LastModifiedVO lastModified() {return lastModified;}
    public Boolean isUnique() {return isUnique;}

}

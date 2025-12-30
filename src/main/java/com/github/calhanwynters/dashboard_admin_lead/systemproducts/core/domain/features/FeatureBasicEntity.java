package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

/*
 ** FeatureBasicEntity extends FeatureAbstractClass
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

public class FeatureBasicEntity extends FeatureAbstractClass {

    public FeatureBasicEntity(
            PkIdVO featureId,
            UuIdVO featureUuId,
            NameVO featureName,
            LabelVO featureLabel,
            DescriptionVO featureDescription,
            StatusEnums featureStatus,
            VersionVO featureVersion,
            LastModifiedVO lastModified,
            Boolean isUnique
            ) {
        super(featureId, featureUuId, featureName, featureLabel, featureDescription, featureStatus, featureVersion, lastModified, isUnique);
    }
}

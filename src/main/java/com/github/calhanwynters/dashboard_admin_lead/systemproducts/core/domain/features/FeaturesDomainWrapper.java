package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.common.Label;
import com.github.calhanwynters.dashboard_admin_lead.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

public interface FeaturesDomainWrapper {
    record FeatureId(PkId value) {}
    record FeatureUuId(UuId value) {}
    record FeatureBusinessUuId(UuId value) {}
    record FeatureName(Name value) {}
    record FeatureLabel (Label value) {}
}

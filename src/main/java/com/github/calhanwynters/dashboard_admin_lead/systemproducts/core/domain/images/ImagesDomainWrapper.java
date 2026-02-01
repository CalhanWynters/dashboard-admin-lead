package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images;

import com.github.calhanwynters.dashboard_admin_lead.common.Description;
import com.github.calhanwynters.dashboard_admin_lead.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

public interface ImagesDomainWrapper {
    record ImageId(PkId value) {}
    record ImageUuId(UuId value) {}
    record ImageBusinessUuId(UuId value) {}
    record ImageName(Name value) {}
    record ImageDescription(Description value) {}
    // URL
}

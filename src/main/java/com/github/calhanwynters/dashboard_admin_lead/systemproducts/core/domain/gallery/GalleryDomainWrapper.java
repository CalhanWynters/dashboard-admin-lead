package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

public interface GalleryDomainWrapper {
    record GalleryId(PkId value) {}
    record GalleryUuId(UuId value) {}
    record GalleryBusinessUuId(UuId value) {}
}

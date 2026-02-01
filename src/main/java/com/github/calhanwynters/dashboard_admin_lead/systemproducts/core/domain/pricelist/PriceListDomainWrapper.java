package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.Version;

public interface PriceListDomainWrapper {
    record PriceListId(PkId value) {}
    record PriceListUuId(UuId value) {}
    record PriceListBusinessUuId(UuId value) {}
    record PriceListVersion(Version value) {}// Version
}

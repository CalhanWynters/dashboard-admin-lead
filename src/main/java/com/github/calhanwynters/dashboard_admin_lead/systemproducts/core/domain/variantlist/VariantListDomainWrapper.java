package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist;

import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

public interface VariantListDomainWrapper {
    record VariantListId(PkId value) {}
    record VariantListUuId(UuId value) {}
    record VariantListBusinessUuId(UuId value) {}
}

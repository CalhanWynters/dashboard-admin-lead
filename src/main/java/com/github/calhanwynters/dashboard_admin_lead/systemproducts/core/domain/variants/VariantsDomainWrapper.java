package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants;

import com.github.calhanwynters.dashboard_admin_lead.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

public interface VariantsDomainWrapper {
    record VariantsId(PkId value) {}
    record VariantsUuId(UuId value) {}
    record VariantsBusinessUuId(UuId value) {}
    record VariantsName(Name value) {}
}

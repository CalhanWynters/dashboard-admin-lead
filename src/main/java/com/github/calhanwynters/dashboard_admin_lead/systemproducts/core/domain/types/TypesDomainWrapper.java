package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types;

import com.github.calhanwynters.dashboard_admin_lead.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

public interface TypesDomainWrapper {
    record TypesId(PkId value) {}
    record TypesUuId(UuId value) {}
    record TypesBusinessUuId(UuId value) {}
    record TypesName(Name value) {}
}

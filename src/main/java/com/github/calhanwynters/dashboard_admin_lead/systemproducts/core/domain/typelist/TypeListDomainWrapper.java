package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist;

import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

public interface TypeListDomainWrapper {
    record TypeListId(PkId value) {}
    record TypeListUuId(UuId value) {}
    record TypeListBusinessUuId(UuId value) {}
}

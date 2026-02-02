package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist;

import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

public interface TypeListDomainWrapper {
    record TypeListId(PkId value) {}
    public record TypeListUuId(UuId value) {
        // Fix: Pass the 36-character Nil UUID string to match UuId record definition
        public static final TypeListUuId NONE = new TypeListUuId(new UuId("00000000-0000-0000-0000-000000000000"));

        public boolean isNone() {
            return this.equals(NONE);
        }
    }
    record TypeListBusinessUuId(UuId value) {}
}

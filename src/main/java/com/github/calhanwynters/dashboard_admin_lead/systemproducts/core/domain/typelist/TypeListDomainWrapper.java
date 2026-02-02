package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist;

import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

public interface TypeListDomainWrapper {
    record TypeListId(PkId value) {
        public static final TypeListId NONE = new TypeListId(PkId.of(0L));
        // Convenience for string-based nil UUIDs if PkId supports it
        public static TypeListId nil() { return NONE; }
    }

    record TypeListUuId(UuId value) {
        public static final TypeListUuId NONE = new TypeListUuId(new UuId("00000000-0000-0000-0000-000000000000"));

        public static TypeListUuId generate() {
            return new TypeListUuId(UuId.generate()); // Assuming UuId.generate() exists
        }

        public boolean isNone() { return this.equals(NONE); }
    }
    record TypeListBusinessUuId(UuId value) {}
}

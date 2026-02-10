package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types;

import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.PhysicalSpecs;

public interface TypesDomainWrapper {
    record TypesId(PkId value) {
        public static final TypesId NONE = new TypesId(PkId.of(0L));
        public static TypesId of(long id) { return new TypesId(PkId.of(id)); }
    }
    record TypesUuId(UuId value) {
        public static TypesUuId generate() {
            return new TypesUuId(UuId.generate());
        }
    }
    record TypesBusinessUuId(UuId value) {}
    record TypesName(Name value) {}

    record TypesPhysicalSpecs(PhysicalSpecs value) {
        public static final TypesPhysicalSpecs NONE = new TypesPhysicalSpecs(PhysicalSpecs.NONE);

        public boolean isNone() {
            return value == null || value.isNone();
        }
    }
}

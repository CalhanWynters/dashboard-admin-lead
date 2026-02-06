package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants;

import com.github.calhanwynters.dashboard_admin_lead.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

public interface VariantsDomainWrapper {

    record VariantsId(PkId value) {
        public static final VariantsId NONE = new VariantsId(PkId.of(0L));

        public static VariantsId of(long id) {
            return new VariantsId(PkId.of(id));
        }
    }

    record VariantsUuId(UuId value) {
        public static final VariantsUuId NONE = new VariantsUuId(UuId.NONE);

        public static VariantsUuId generate() {
            return new VariantsUuId(UuId.generate());
        }

        public boolean isNone() {
            return value != null && value.isNone();
        }
    }

    record VariantsBusinessUuId(UuId value) {}
    record VariantsName(Name value) {}
}

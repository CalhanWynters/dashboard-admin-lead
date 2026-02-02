package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist;

import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

public interface VariantListDomainWrapper {

    record VariantListId(PkId value) {
        public static final VariantListId NONE = new VariantListId(PkId.of(0L));

        // Fix: Allows VariantListId.of(0L)
        public static VariantListId of(long id) {
            return new VariantListId(PkId.of(id));
        }
    }

    record VariantListUuId(UuId value) {
        public static final VariantListUuId NONE = new VariantListUuId(UuId.NONE);

        // Fix: Allows VariantListUuId.generate()
        public static VariantListUuId generate() {
            return new VariantListUuId(UuId.generate());
        }

        public boolean isNone() {
            return this.value != null && this.value.isNone();
        }
    }

    record VariantListBusinessUuId(UuId value) {}
}

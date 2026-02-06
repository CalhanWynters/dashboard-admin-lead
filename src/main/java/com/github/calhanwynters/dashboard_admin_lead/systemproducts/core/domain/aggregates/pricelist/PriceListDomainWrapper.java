package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.Version;

public interface PriceListDomainWrapper {
    record PriceListId(PkId value) {
        public static PriceListId of(long id) { return new PriceListId(PkId.of(id)); }
    }

    record PriceListUuId(UuId value) {
        public static final PriceListUuId NONE = new PriceListUuId(UuId.NONE);
        public static PriceListUuId generate() { return new PriceListUuId(UuId.generate()); }
        public boolean isNone() { return this.value.isNone(); }
    }

    record PriceListBusinessUuId(UuId value) {}

    record PriceListVersion(Version value) {
        public static final PriceListVersion INITIAL = new PriceListVersion(Version.INITIAL);
    }
}

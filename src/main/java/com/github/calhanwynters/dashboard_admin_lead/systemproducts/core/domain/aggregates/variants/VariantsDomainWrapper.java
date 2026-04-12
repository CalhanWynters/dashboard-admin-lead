package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants;

import com.github.calhanwynters.dashboard_admin_lead.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.Region;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

/**
 * Interface must be PUBLIC.
 * Records inside are implicitly public.
 */
public interface VariantsDomainWrapper {

    record VariantsId(PkId value) {
        public static final VariantsId NONE = new VariantsId(new PkId(0L));
        public static VariantsId of(long id) { return new VariantsId(new PkId(id)); }
    }

    record VariantsUuId(UuId value) {
        public static final VariantsUuId NONE = new VariantsUuId(UuId.NONE);
        public static VariantsUuId generate() { return new VariantsUuId(UuId.generate()); }
        public boolean isNone() { return value != null && value.isNone(); }
    }

    record VariantsRegion(Region value) {
        public static VariantsRegion of(VariantsRegion other) {
            return new VariantsRegion(other.value());
        }

        // Allows creating directly from the common Region VO
        public static VariantsRegion from(Region region) {
            return new VariantsRegion(region);
        }
    }

    record VariantsBusinessUuId(UuId value) {}

    record VariantsName(Name value) {}
}

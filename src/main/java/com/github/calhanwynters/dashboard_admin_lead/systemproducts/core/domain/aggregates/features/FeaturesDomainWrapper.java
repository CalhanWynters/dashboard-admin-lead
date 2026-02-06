package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features;

import com.github.calhanwynters.dashboard_admin_lead.common.Label;
import com.github.calhanwynters.dashboard_admin_lead.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

public interface FeaturesDomainWrapper {

    record FeatureId(PkId value) {
        public static final FeatureId NONE = new FeatureId(PkId.of(0L));

        public static FeatureId of(long id) {
            return new FeatureId(PkId.of(id));
        }
    }

    record FeatureUuId(UuId value) {
        public static final FeatureUuId NONE = new FeatureUuId(UuId.NONE);

        public static FeatureUuId generate() {
            return new FeatureUuId(UuId.generate());
        }

        public boolean isNone() {
            return value != null && value.isNone();
        }
    }

    record FeatureBusinessUuId(UuId value) {}
    record FeatureName(Name value) {}
    record FeatureLabel(Label value) {}

    // Define a Discriminated Key for Compatibility services.
    record TypedTrigger(UuId id, TriggerType type) {
        public enum TriggerType { FEATURE, TYPE }

        public static TypedTrigger feature(UuId id) { return new TypedTrigger(id, TriggerType.FEATURE); }
        public static TypedTrigger type(UuId id) { return new TypedTrigger(id, TriggerType.TYPE); }
    }
}

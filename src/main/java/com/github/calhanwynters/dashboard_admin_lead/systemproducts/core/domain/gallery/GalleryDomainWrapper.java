package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

public interface GalleryDomainWrapper {
    record GalleryId(PkId value) {}

    record GalleryUuId(UuId value) {
        // Sentinel for "no gallery"
        public static final GalleryUuId NONE = new GalleryUuId(UuId.NONE);

        // Fix: Resolve GalleryUuId.generate() in the Factory
        public static GalleryUuId generate() {
            return new GalleryUuId(UuId.generate());
        }

        public boolean isNone() {
            return this.value.isNone();
        }
    }

    record GalleryBusinessUuId(UuId value) {}
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

public interface GalleryDomainWrapper {

    record GalleryId(PkId value) {
        public static final GalleryId NONE = new GalleryId(PkId.of(0L));

        // Fix: Resolve GalleryId.of(0L) in the Factory
        public static GalleryId of(long id) {
            return new GalleryId(PkId.of(id));
        }
    }

    record GalleryUuId(UuId value) {
        public static final GalleryUuId NONE = new GalleryUuId(UuId.NONE);

        public static GalleryUuId generate() {
            return new GalleryUuId(UuId.generate());
        }

        public boolean isNone() {
            return this.value != null && this.value.isNone();
        }
    }

    record GalleryBusinessUuId(UuId value) {}
}

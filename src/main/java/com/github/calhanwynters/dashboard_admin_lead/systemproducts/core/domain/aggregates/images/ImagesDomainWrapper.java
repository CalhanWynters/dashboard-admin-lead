package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images;

import com.github.calhanwynters.dashboard_admin_lead.common.Description;
import com.github.calhanwynters.dashboard_admin_lead.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

public interface ImagesDomainWrapper {
    record ImageId(PkId value) {
        public static final ImageId NONE = new ImageId(PkId.of(0L));

        // Fixes: ImageId.of(0L)
        public static ImageId of(long id) {
            return new ImageId(PkId.of(id));
        }
    }

    record ImageUuId(UuId value) {
        public static final ImageUuId NONE = new ImageUuId(UuId.NONE);

        // Fixes: ImageUuId.generate()
        public static ImageUuId generate() {
            return new ImageUuId(UuId.generate());
        }

        public boolean isNone() {
            return value != null && value.isNone();
        }
    }

    record ImagesBusinessUuId(UuId value) {}
    record ImageName(Name value) {}
    record ImageDescription(Description value) {}

    // Added to satisfy the 'url' parameter in your aggregate return
    record ImageUrl(String value) {
        public static ImageUrl of(String url) {
            return new ImageUrl(url);
        }
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;

public interface ProductDomainWrapper {

    record ProductId(PkId value) {
        public static ProductId of(long id) {
            return new ProductId(PkId.of(id));
        }
    }

    record ProductUuId(UuId value) {
        public static ProductUuId generate() {
            return new ProductUuId(UuId.generate());
        }
    }

    record ProductManifest(
            ProductName name,
            ProductCategory category,
            ProductDescription description
    ) {
        public ProductManifest {
            DomainGuard.notNull(name, "Product Name");
            DomainGuard.notNull(category, "Category");
            DomainGuard.notNull(description, "Description");
        }
    }

    record ProductBusinessUuId(UuId value) {}
    record ProductName(Name value) {}
    record ProductCategory(Category value) {}

    record ProductVersion(Version value) {
        public static final ProductVersion INITIAL = new ProductVersion(Version.INITIAL);
    }

    record ProductDescription(Description value) {}

    record ProductStatus(StatusEnums value) {
        public static final ProductStatus DRAFT = new ProductStatus(StatusEnums.DRAFT);
        public static final ProductStatus ACTIVE = new ProductStatus(StatusEnums.ACTIVE);
        public static final ProductStatus INACTIVE = new ProductStatus(StatusEnums.INACTIVE);
        public static final ProductStatus DISCONTINUED = new ProductStatus(StatusEnums.DISCONTINUED);

        public boolean canTransitionTo(ProductStatus nextStatus) {
            DomainGuard.notNull(nextStatus, "Target Status");
            return this.value.canTransitionTo(nextStatus.value());
        }
    }

    // --- Physical Traits with Null Object Constants ---

    /**
     * Composite trait for Product.
     * Groups shared PhysicalSpecs into the Product Domain.
     */
    record ProductPhysicalSpecs(PhysicalSpecs value) {
        public static final ProductPhysicalSpecs NONE = new ProductPhysicalSpecs(PhysicalSpecs.NONE);

        public boolean isNone() {
            return value == null || value.isNone();
        }

        // Convenience mappers to keep the Aggregate API clean
        public ProductWeight weight() { return new ProductWeight(value.weight()); }
        public ProductDimensions dimensions() { return new ProductDimensions(value.dimensions()); }
        public ProductCareInstructions careInstructions() { return new ProductCareInstructions(value.careInstructions()); }
    }

    record ProductWeight(Weight value) {
        public static final ProductWeight NONE = new ProductWeight(Weight.NONE);
        public boolean isNone() { return this.value == null || this.value.equals(Weight.NONE); }
    }

    record ProductDimensions(Dimensions value) {
        public static final ProductDimensions NONE = new ProductDimensions(Dimensions.NONE);
        public boolean isNone() { return this.value == null || this.value.equals(Dimensions.NONE); }
    }

    record ProductCareInstructions(CareInstruction value) {
        public static final ProductCareInstructions NONE = new ProductCareInstructions(CareInstruction.NONE);
        public boolean isNone() { return this.value == null || this.value.equals(CareInstruction.NONE); }
    }

    record GalleryUuId(UuId value) {
        public static final GalleryUuId NONE = new GalleryUuId(UuId.NONE);

        public static GalleryUuId generate() {
            return new GalleryUuId(UuId.generate());
        }

        public boolean isNone() {
            return this.value.isNone();
        }
    }
}


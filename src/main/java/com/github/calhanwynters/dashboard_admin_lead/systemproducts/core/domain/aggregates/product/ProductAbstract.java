package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryCollection;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import java.util.Objects;

public abstract class ProductAbstract {

    private final PkId productId;
    private final UuId productUuId;
    private final Name productName;
    private final Category category;
    private final Description productDesc;
    private final StatusEnums productStatus;
    private final Version productVersion;
    private final AuditMetadata audit;
    private final GalleryCollection galleryCollection;

    protected ProductAbstract(Builder<?, ?> builder) {
        this.productId = Objects.requireNonNull(builder.productId, "Product ID cannot be null");
        this.productUuId = Objects.requireNonNull(builder.productUuId, "Product UUID cannot be null");
        this.productName = Objects.requireNonNull(builder.productName, "Product Name cannot be null");
        this.category = Objects.requireNonNull(builder.category, "Category cannot be null");
        this.productDesc = Objects.requireNonNull(builder.productDesc, "Description cannot be null");
        this.productStatus = Objects.requireNonNull(builder.productStatus, "Status cannot be null");
        this.productVersion = Objects.requireNonNull(builder.productVersion, "Version cannot be null");
        this.audit = Objects.requireNonNull(builder.audit, "Audit metadata cannot be null");
        this.galleryCollection = Objects.requireNonNull(builder.galleryCollection, "GalleryCollection cannot be null");
    }

    public abstract static class Builder<T extends ProductAbstract, B extends Builder<T, B>> {
        protected PkId productId;
        protected UuId productUuId;
        protected Name productName;
        protected Category category;
        protected Description productDesc;
        protected StatusEnums productStatus;
        protected Version productVersion;
        protected AuditMetadata audit;
        protected GalleryCollection galleryCollection;

        protected abstract B self();
        public abstract T build();

        public B productId(PkId productId) { this.productId = productId; return self(); }
        public B productUuId(UuId productUuId) { this.productUuId = productUuId; return self(); }
        public B productName(Name productName) { this.productName = productName; return self(); }
        public B category(Category category) { this.category = category; return self(); }
        public B productDesc(Description productDesc) { this.productDesc = productDesc; return self(); }
        public B productStatus(StatusEnums productStatus) { this.productStatus = productStatus; return self(); }
        public B productVersion(Version productVersion) { this.productVersion = productVersion; return self(); }
        public B audit(AuditMetadata audit) { this.audit = audit; return self(); }
        public B galleryCollection(GalleryCollection galleryCollection) { this.galleryCollection = galleryCollection; return self(); }
    }

    // Getters
    public PkId getProductId() { return productId; }
    public UuId getProductUuId() { return productUuId; }
    public Name getProductName() { return productName; }
    public Category getCategory() { return category; }
    public Description getProductDesc() { return productDesc; }
    public StatusEnums getProductStatus() { return productStatus; }
    public Version getProductVersion() { return productVersion; }
    public AuditMetadata getAudit() { return audit; }
    public GalleryCollection getGalleryCollection() { return galleryCollection; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductAbstract that)) return false;
        return Objects.equals(this.productUuId, that.productUuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productUuId);
    }
}

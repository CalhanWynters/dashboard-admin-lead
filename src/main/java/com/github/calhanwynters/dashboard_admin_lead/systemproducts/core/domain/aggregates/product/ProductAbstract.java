package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.Category;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.Description;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.ImageUrl;

import java.util.List;
import java.util.Objects;

public abstract class ProductAbstract {

    private final PkId productId;
    private final UuId productUuId;
    private final Name productName;
    private final Category category;
    private final Description productDesc;
    private final StatusEnums productStatus;
    private final Version productVersion;
    private final AuditMetadata audit; // Groups CreatedAt and LastModified
    private final List<ImageUrl> images;

    protected ProductAbstract(Builder<?, ?> builder) {
        this.productId = Objects.requireNonNull(builder.productId, "Product ID cannot be null");
        this.productUuId = Objects.requireNonNull(builder.productUuId, "Product UUID cannot be null");
        this.productName = Objects.requireNonNull(builder.productName, "Product Name cannot be null");
        this.category = Objects.requireNonNull(builder.category, "Category cannot be null");
        this.productDesc = Objects.requireNonNull(builder.productDesc, "Description cannot be null");
        this.productStatus = Objects.requireNonNull(builder.productStatus, "Status cannot be null");
        this.productVersion = Objects.requireNonNull(builder.productVersion, "Version cannot be null");
        this.audit = Objects.requireNonNull(builder.audit, "Audit metadata cannot be null");

        Objects.requireNonNull(builder.images, "Images list cannot be null");
        this.images = List.copyOf(builder.images);
    }

    /**
     * Recursive Generic Builder
     * @param <T> The Product subtype
     * @param <B> The Builder subtype
     */
    public abstract static class Builder<T extends ProductAbstract, B extends Builder<T, B>> {
        private PkId productId;
        private UuId productUuId;
        private UuId businessId;
        private Name productName;
        private Category category;
        private Description productDesc;
        private Description shippingPolicy;
        private StatusEnums productStatus;
        private Version productVersion;
        private AuditMetadata audit;
        private List<ImageUrl> images;            // Consider decoupling this into a Gallery Aggregate
        private ImageUrl thumbnail;

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
        public B images(List<ImageUrl> images) { this.images = images; return self(); }
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
    public List<ImageUrl> getImages() { return images; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductAbstract that)) return false;
        return Objects.equals(productUuId, that.productUuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productUuId);
    }
}

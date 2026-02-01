package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.product;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.IncompatibilityRule;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.validationchecks.DomainGuard;
import java.util.Optional;
import java.util.Set;

/**
 * Aggregate Root: Product (2026 Edition)
 * Optimized for PostgreSQL JSONB performance and Multi-tenant scaling.
 * <p>
 * Update: Implements strict XOR logic for Standard (Type-based) vs. Bespoke attributes.
 */
public class ProductAggregateRoot {
    // Identity & Tenant Metadata
    private final PkId productId;
    private final UuId productUuId;
    private final UuId businessId;

    // Core Attributes
    private final Name productName;
    private final Category productCategory;
    private final Description productDesc;
    private final StatusEnums productStatus;
    private final Version productVersion;
    private final AuditMetadata audit;

    // Bespoke Physical & Financial Attributes (Nullable Overrides)
    private final Dimensions productDimensions;
    private final Weight productWeight;
    private final CareInstruction productCareInstruction;

    // Decoupled Aggregate References (Composition)
    private final UuId galleryColId;
    private final UuId typeColId;       // Template reference (Nullable)
    private final UuId variantColId;

    // Incompatibility Rules
    private final Set<IncompatibilityRule> internalRules;
    private final Set<IncompatibilityRule> contextualRules;

    /**
     * Package-private constructor: Enforced by ProductFactory.
     */
    ProductAggregateRoot(PkId productId, UuId productUuId, UuId businessId, Name productName,
                         Category productCategory, Description productDesc, StatusEnums productStatus,
                         Version productVersion, AuditMetadata audit, UuId galleryColId, UuId typeColId,
                         UuId variantColId, Dimensions productDimensions, Weight productWeight,
                         CareInstruction productCareInstruction, Set<IncompatibilityRule> internalRules,
                         Set<IncompatibilityRule> contextualRules) {

        // Invariant Guarding
        DomainGuard.notNull(productUuId, "Product Identity");
        DomainGuard.notNull(businessId, "Business Identity");
        DomainGuard.notNull(productName, "Product featuresName");
        DomainGuard.notNull(productCategory, "Product Category");
        DomainGuard.notNull(productStatus, "Product Status");
        DomainGuard.notNull(galleryColId, "Gallery Reference");

        this.productId = productId;
        this.productUuId = productUuId;
        this.businessId = businessId;
        this.productName = productName;
        this.productCategory = productCategory;
        this.productDesc = productDesc;
        this.productStatus = productStatus;
        this.productVersion = productVersion;
        this.audit = audit;
        this.galleryColId = galleryColId;
        this.typeColId = typeColId;
        this.variantColId = variantColId;

        // Assignments for Physical/Financial state
        this.productDimensions = productDimensions;
        this.productWeight = productWeight;
        this.productCareInstruction = productCareInstruction;

        // Defensive Copying for Rules
        this.internalRules = internalRules == null ? Set.of() : Set.copyOf(internalRules);
        this.contextualRules = contextualRules == null ? Set.of() : Set.copyOf(contextualRules);

        // Core Business Rule Enforcement
        validateAttributeSourceInvariants();
    }

    /**
     * Validates that a Product does not mix Template (Type) data with Bespoke data.
     * Rule: If TypeColId exists, all bespoke physical/financial fields must be null.
     */
    private void validateAttributeSourceInvariants() {
        boolean hasTypeTemplate = typeColId != null;
        boolean hasBespokeData = productDimensions != null || productWeight != null ||
                productCareInstruction != null;

        if (hasTypeTemplate && hasBespokeData) {
            throw new IllegalStateException("Domain Violation: Product cannot mix a Type template with bespoke attributes.");
        }

        if (!hasTypeTemplate && !hasBespokeData) {
            throw new IllegalStateException("Domain Violation: Product must have either a Type template or bespoke attributes.");
        }
    }

    // ======================== Accessors (Bespoke Overrides) ============================

    public Optional<Dimensions> getProductDimensions() { return Optional.ofNullable(productDimensions); }
    public Optional<Weight> getProductWeight() { return Optional.ofNullable(productWeight); }
    public Optional<CareInstruction> getProductCareInstruction() { return Optional.ofNullable(productCareInstruction); }

    // ======================== Standard Accessors ============================

    public PkId getProductId() { return productId; }
    public UuId getProductUuId() { return productUuId; }
    public UuId getBusinessId() { return businessId; }
    public Name getProductName() { return productName; }
    public Category getProductCategory() { return productCategory; }
    public Description getProductDesc() { return productDesc; }
    public StatusEnums getStatus() { return productStatus; }
    public Version getVersion() { return productVersion; }
    public AuditMetadata getAudit() { return audit; }
    public UuId getGalleryColId() { return galleryColId; }
    public Optional<UuId> getTypeColId() { return Optional.ofNullable(typeColId); }
    public Optional<UuId> getVariantColId() { return Optional.ofNullable(variantColId); }
    public Set<IncompatibilityRule> getInternalRules() { return internalRules; }
    public Set<IncompatibilityRule> getContextualRules() { return contextualRules; }

    public ProductBehavior act() { return new ProductBehavior(this); }
}

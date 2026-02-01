package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.product;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.IncompatibilityRule;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.*;
import java.util.Set;

/**
 * Domain Factory for Product Aggregates (2026 Edition).
 * Enforces the XOR invariant and handles all four product permutations:
 * 1. Bespoke Standalone (Base Product)
 * 2. Bespoke with Variants (ProductVariant)
 * 3. Standard Standalone (ProductTyped)
 * 4. Standard with Variants (ProductTypedVariant)
 */
public class ProductFactory {

    /**
     * PERMUTATION 1: Product (Bespoke Standalone)
     * A unique product with its own specs and no variants.
     */
    public static ProductAggregateRoot createBespokeStandalone(
            UuId businessId, Name name, Category category, Description desc,
            Dimensions dim, Weight weight, CareInstruction care) {

        return createBespoke(businessId, name, category, desc, UuId.NONE, dim, weight, care);
    }

    /**
     * PERMUTATION 2: ProductVariant (Bespoke with Variants)
     * A unique product architecture that supports configurable options.
     */
    public static ProductAggregateRoot createBespokeWithVariants(
            UuId businessId, Name name, Category category, Description desc,
            UuId variantColId, Dimensions dim, Weight weight, CareInstruction care) {

        return createBespoke(businessId, name, category, desc, variantColId, dim, weight, care);
    }

    /**
     * PERMUTATION 3: ProductTyped (Standard Standalone)
     * A simple reflection of a Type template without variants.
     */
    public static ProductAggregateRoot createStandardStandalone(
            UuId businessId, Name name, Category category, Description desc,
            UuId typeColId) {

        return createStandard(businessId, name, category, desc, typeColId, UuId.NONE);
    }

    /**
     * PERMUTATION 4: ProductTypedVariant (Standard with Variants)
     * A standardized template that supports specific configurable features.
     */
    public static ProductAggregateRoot createStandardWithVariants(
            UuId businessId, Name name, Category category, Description desc,
            UuId typeColId, UuId variantColId) {

        return createStandard(businessId, name, category, desc, typeColId, variantColId);
    }

    // ======================== Internal Orchestration ============================

    private static ProductAggregateRoot createStandard(
            UuId businessId, Name name, Category category, Description desc,
            UuId typeColId, UuId variantColId) {

        return new ProductAggregateRoot(
                PkId.of(0L),
                UuId.generate(),
                businessId,
                name,
                category,
                desc,
                StatusEnums.DRAFT,
                Version.INITIAL,
                AuditMetadata.create(),
                UuId.generate(),    // galleryColId
                typeColId,
                variantColId,
                Dimensions.NONE,
                Weight.NONE,
                CareInstruction.NONE,
                Set.of(),           // internalRules
                Set.of()            // contextualRules
        );
    }

    private static ProductAggregateRoot createBespoke(
            UuId businessId, Name name, Category category, Description desc,
            UuId variantColId, Dimensions dimensions, Weight weight,
            CareInstruction care) {

        return new ProductAggregateRoot(
                PkId.of(0L),
                UuId.generate(),
                businessId,
                name,
                category,
                desc,
                StatusEnums.DRAFT,
                Version.INITIAL,
                AuditMetadata.create(),
                UuId.generate(),
                UuId.NONE,
                variantColId,
                dimensions,
                weight,
                care,
                Set.of(),           // internalRules
                Set.of()            // contextualRules
        );
    }

    public static ProductAggregateRoot reconstitute(
            PkId productId, UuId productUuId, UuId businessId, Name name,
            Category category, Description desc, StatusEnums status,
            Version version, AuditMetadata audit, UuId galleryColId,
            UuId typeColId, UuId variantColId, Dimensions dim,
            Weight weight, CareInstruction care,
            Set<IncompatibilityRule> internal, Set<IncompatibilityRule> contextual) {

        return new ProductAggregateRoot(
                productId, productUuId, businessId, name, category, desc,
                status, version, audit, galleryColId, typeColId, variantColId,
                dim, weight, care, internal, contextual
        );
    }

}

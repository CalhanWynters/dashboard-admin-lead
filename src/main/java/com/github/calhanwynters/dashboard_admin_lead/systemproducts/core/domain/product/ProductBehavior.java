package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.*;

public final class ProductBehavior {

    public static void validateStatusTransition(ProductStatus current, ProductStatus next) {
        DomainGuard.ensure(
                current.canTransitionTo(next),
                "Illegal transition from %s to %s.".formatted(current, next),
                "VAL-016", "STATE_VIOLATION"
        );
    }

    public static void validateComposition(ProductAggregateRoot product) {
        DomainGuard.ensure(
                new ProductCompositionSpecification().isSatisfiedBy(product),
                "Invalid Product Type: Standard products (with TypeList) cannot have local Price/Specs. " +
                        "Bespoke products (without TypeList) require both Price and Specs.",
                "VAL-016", "INVARIANT_VIOLATION"
        );
    }

    public static ProductVersion incrementVersion(ProductVersion current) {
        return new ProductVersion(current.value().next());
    }

    public static void validateManifest(ProductManifest manifest) {
        DomainGuard.notNull(manifest, "Product Manifest");
    }

    public static void validatePhysicalSpecs(ProductPhysicalSpecs specs) {
        DomainGuard.notNull(specs, "Physical Specs");
    }
}

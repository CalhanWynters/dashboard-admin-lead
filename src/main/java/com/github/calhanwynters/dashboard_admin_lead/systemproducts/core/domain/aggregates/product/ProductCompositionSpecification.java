package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;

/**
 * Refactored Product Composition Specification (2026 Edition).
 * Implements structural business rules for Standard vs Bespoke products.
 */
public class ProductCompositionSpecification implements Specification<ProductAggregate> {

    @Override
    public boolean isSatisfiedBy(ProductAggregate product) {
        boolean hasType = !product.getTypeListUuId().equals(TypeListUuId.NONE);
        boolean hasPrice = !product.getPriceListUuId().equals(PriceListUuId.NONE);
        boolean hasSpecs = !product.getPhysicalSpecs().isNone();

        // Standard Rules (Type 2 & 4): TypeList is PRESENT
        if (hasType) {
            // Must NOT have local Price or Specs (inherited from Type).
            return !hasPrice && !hasSpecs;
        }

        // Bespoke Rules (Type 1 & 3): TypeList is NONE
        else {
            // Must explicitly HAVE local Price and Specs.
            return hasPrice && hasSpecs;
        }
    }
}

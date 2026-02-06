package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;

public class ProductCompositionSpecification implements Specification<ProductAggregateRoot> {

    @Override
    public boolean isSatisfiedBy(ProductAggregateRoot product) {
        boolean hasType = !product.getTypeListUuId().equals(TypeListUuId.NONE);
        boolean hasVariant = !product.getVariantListUuId().equals(VariantListUuId.NONE);
        boolean hasPrice = !product.getPriceListUuId().equals(PriceListUuId.NONE);
        boolean hasSpecs = !product.getProductPhysicalSpecs().isNone();

        // Standard Rules (Type 2 & 4): TypeList is PRESENT
        if (hasType) {
            // Must NOT have Price or Specs. Variant can be YES (Type 4) or NONE (Type 2).
            return !hasPrice && !hasSpecs;
        }

        // Bespoke Rules (Type 1 & 3): TypeList is NONE
        else {
            // Must HAVE Price and Specs. Variant can be YES (Type 3) or NONE (Type 1).
            return hasPrice && hasSpecs;
        }
    }
}

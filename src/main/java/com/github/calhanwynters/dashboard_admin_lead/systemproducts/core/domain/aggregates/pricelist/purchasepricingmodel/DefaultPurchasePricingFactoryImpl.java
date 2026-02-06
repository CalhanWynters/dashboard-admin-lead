package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel;

import java.util.Currency;

public class DefaultPurchasePricingFactoryImpl implements PurchasePricingFactory {
    @Override
    public SimplePurchasePricing createFixedPurchase(Money fixedPrice) {
        return new PriceFixedPurchase(fixedPrice);
    }

    @Override
    public SimplePurchasePricing createNonePurchase(Currency currency) {
        return new PriceNonePurchase(currency);
    }
}

/* NOTES
 * This implementation is meant to implement standard pricing strategies for "Type" and "Product" Aggregates
 * depending on the composition of the instance of the product.
 * If "Variant" Aggregate is part of the instance composition then the Features Entity will contain the other concrete
 * class implementations of PurchasePrice within the variant composition can be a part of the instance.
 */
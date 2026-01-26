package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel;

import java.util.Currency;

public class DefaultPurchasePricingFactory implements PurchasePricingFactory {
    @Override
    public SimplePurchasePricing createFixedPurchase(Money fixedPrice) {
        return new PriceFixedPurchase(fixedPrice);
    }

    @Override
    public SimplePurchasePricing createNonePurchase(Currency currency) {
        return new PriceNonePurchase(currency);
    }
}


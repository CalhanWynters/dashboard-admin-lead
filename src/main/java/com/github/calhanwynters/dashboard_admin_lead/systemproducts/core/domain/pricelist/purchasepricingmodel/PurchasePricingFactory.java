package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.purchasepricingmodel;

import java.util.Currency;

public interface PurchasePricingFactory {
    SimplePurchasePricing createFixedPurchase(Money fixedPrice);
    SimplePurchasePricing createNonePurchase(Currency currency);
}

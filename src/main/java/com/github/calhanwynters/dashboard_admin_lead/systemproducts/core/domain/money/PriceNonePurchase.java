package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.money;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public record PriceNonePurchase(Currency currency) implements PurchasePricing {

    public PriceNonePurchase {
        Objects.requireNonNull(currency, "Currency context is required even for PriceNone.");
    }

    @Override
    public Money calculate(BigDecimal quantity) {
        // Return a valid Money with amount 0 instead of null
        return Money.zero(currency);
    }
}

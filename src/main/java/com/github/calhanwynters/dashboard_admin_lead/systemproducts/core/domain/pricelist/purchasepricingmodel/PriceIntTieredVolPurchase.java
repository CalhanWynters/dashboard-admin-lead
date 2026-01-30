package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.purchasepricingmodel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record PriceIntTieredVolPurchase(List<TierBucket> buckets) implements PurchasePricing {

    public record TierBucket(BigDecimal minQty, BigDecimal maxQty, Money pricePerUnit) {
        public boolean contains(BigDecimal quantity) {
            // Logic: min <= quantity < max (standard half-open interval)
            return quantity.compareTo(minQty) >= 0 &&
                    (maxQty == null || quantity.compareTo(maxQty) < 0);
        }
    }

    public PriceIntTieredVolPurchase {
        Objects.requireNonNull(buckets, "Buckets list cannot be null");
        if (buckets.isEmpty()) throw new IllegalArgumentException("Must have at least one bucket.");
        // Defensive copy
        buckets = List.copyOf(buckets);
    }

    @Override
    public Money calculate(BigDecimal quantity) {
        BigDecimal qty = (quantity == null) ? BigDecimal.ZERO : quantity;

        // Find the specific bucket that covers this quantity
        TierBucket selectedBucket = buckets.stream()
                .filter(bucket -> bucket.contains(qty))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No pricing tier found for quantity: " + qty));

        // Assuming Volume Pricing: Total = (Rate for that bucket) * (Quantity)
        return selectedBucket.pricePerUnit().multiply(qty.intValueExact());
    }
}

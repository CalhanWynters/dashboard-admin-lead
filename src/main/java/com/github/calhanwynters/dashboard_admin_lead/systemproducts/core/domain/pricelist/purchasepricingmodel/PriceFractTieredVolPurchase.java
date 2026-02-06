package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.purchasepricingmodel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Implements Volume-based Pricing for fractional quantities (Weighted Goods).
 * The total quantity determines which price bucket is selected,
 * and that bucket's rate is applied to the entire amount.
 */
public record PriceFractTieredVolPurchase(List<TierBucket> buckets) implements PurchasePricing {

    /**
     * Represents a pricing range.
     * Logic: minQty <= quantity < maxQty (half-open interval).
     */
    public record TierBucket(BigDecimal minQty, BigDecimal maxQty, Money pricePerUnit) {
        public TierBucket {
            Objects.requireNonNull(minQty, "minQty cannot be null");
            Objects.requireNonNull(pricePerUnit, "pricePerUnit cannot be null");
        }

        public boolean contains(BigDecimal quantity) {
            return quantity.compareTo(minQty) >= 0 &&
                    (maxQty == null || quantity.compareTo(maxQty) < 0);
        }
    }

    public PriceFractTieredVolPurchase {
        Objects.requireNonNull(buckets, "Buckets list cannot be null");
        if (buckets.isEmpty()) {
            throw new IllegalArgumentException("Must have at least one pricing bucket.");
        }

        // Defensive copy to ensure immutability
        buckets = List.copyOf(buckets);
    }

    /**
     * Calculates total price based on a fractional quantity.
     * Example: 1.5kg @ $10.00/kg = $15.00
     */
    @Override
    public Money calculate(BigDecimal quantity) {
        BigDecimal qty = (quantity == null) ? BigDecimal.ZERO : quantity;

        if (qty.signum() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }

        // Find the specific bucket that covers this fractional quantity
        TierBucket selectedBucket = buckets.stream()
                .filter(bucket -> bucket.contains(qty))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No pricing tier defined for quantity: " + qty));

        // Use the BigDecimal multiply overload to support fractional math
        return selectedBucket.pricePerUnit().multiply(qty);
    }

    @Override
    public PurchasePricing adjustedBy(double factor) {
        BigDecimal bigFactor = BigDecimal.valueOf(factor);

        // Map over the existing buckets to create a new list with adjusted prices
        List<TierBucket> adjustedBuckets = this.buckets.stream()
                .map(bucket -> new TierBucket(
                        bucket.minQty(),
                        bucket.maxQty(),
                        bucket.pricePerUnit().multiply(bigFactor)
                ))
                .toList();

        return new PriceFractTieredVolPurchase(adjustedBuckets);
    }

}
